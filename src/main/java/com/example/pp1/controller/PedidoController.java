package com.example.pp1.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pp1.DTO.pedido.ConfirmarPedidoDTO;
import com.example.pp1.DTO.pedido.CrearPedidoDTO;
import com.example.pp1.Entity.Pedido;
import com.example.pp1.Service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService service;

    // Crear pedido
    @PostMapping
    public ResponseEntity<String> crearPedido(
            @Valid @RequestBody CrearPedidoDTO dto,
            BindingResult resultado) {

        if (resultado.hasErrors()) {
            String errores = resultado.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(" , "));
            return ResponseEntity.badRequest().body(errores);
        }

        PedidoService.respuestaPeticiones respuesta = service.crearPedido(dto);

        if (respuesta == PedidoService.respuestaPeticiones.repetido_pedido) {
            return ResponseEntity.badRequest().body("Ya existe un pedido para ese usuario en esa fecha");
        }

        if(respuesta== PedidoService.respuestaPeticiones.falta_usuario){
            return ResponseEntity.badRequest().body("No se encontro un usuario el usuario correspondiente");
        }

        if (respuesta == PedidoService.respuestaPeticiones.ok) {
            return ResponseEntity.ok("El pedido se creó correctamente");
        }

        return ResponseEntity.badRequest().body("No se pudo crear el pedido");
    }

    // Confirmar un pedido (individual)
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<String> confirmarPedido(@PathVariable Integer id) {
        PedidoService.respuestaPeticiones respuesta = service.confirmarPedido(id);

        if (respuesta == PedidoService.respuestaPeticiones.falta_pedido) {
            return ResponseEntity.status(404).body("No se encontró el pedido");
        }

        return ResponseEntity.ok("El pedido se confirmó correctamente");
    }

    // Cancelar pedido + reponer stock del menú/plato
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelarPedido(
            @PathVariable Integer id,
            @RequestParam Integer idMenuPlato) {

        PedidoService.respuestaPeticiones respuesta = service.cancelarPedido(id, idMenuPlato);

        if (respuesta == PedidoService.respuestaPeticiones.falta_pedido) {
            return ResponseEntity.status(404).body("No se encontró el pedido");
        }

        return ResponseEntity.ok("El pedido se canceló correctamente");
    }

    // Modificar pedido (solo si no está confirmado)
    /*
     * @PutMapping("/{id}")
     * public ResponseEntity<String> modificarPedido(
     * 
     * @PathVariable Integer id,
     * 
     * @Valid @RequestBody ModificarPedidoDTO dto,
     * BindingResult resultado) {
     * 
     * if (resultado.hasErrors()) {
     * String errores = resultado.getFieldErrors().stream()
     * .map(FieldError::getDefaultMessage)
     * .collect(Collectors.joining(" , "));
     * return ResponseEntity.badRequest().body(errores);
     * }
     * 
     * dto.setId(id); // aseguramos que el id venga de la URL
     * 
     * PedidoService.respuestaPeticiones respuesta = service.modificarPedido(dto);
     * 
     * if (respuesta == PedidoService.respuestaPeticiones.falta_pedido) {
     * return ResponseEntity.status(404).body("No se encontró el pedido");
     * }
     * 
     * if (respuesta == PedidoService.respuestaPeticiones.no_editable_pedido) {
     * return
     * ResponseEntity.badRequest().body("No se puede modificar un pedido confirmado"
     * );
     * }
     * 
     * return ResponseEntity.ok("El pedido se modificó correctamente");
     * }
     */

    // Listar pedidos por usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> pedidosPorUsuario(@PathVariable Integer idUsuario) {
        List<Pedido> pedidos = service.pedidoPorUsuario(idUsuario);
        if (pedidos.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontraron pedidos para este usuario");
        }
        return ResponseEntity.ok(pedidos);
    }

    // Listar pedidos por usuario + estado
    @GetMapping("/usuario/{idUsuario}/estado/{estado}")
    public ResponseEntity<?> pedidosPorUsuarioYEstado(
            @PathVariable Integer idUsuario,
            @PathVariable Pedido.EstadosPedidos estado) {

        List<Pedido> pedidos = service.pedidoPorUsuarioYEstado(idUsuario, estado);
        if (pedidos.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontraron pedidos para este usuario con ese estado");
        }
        return ResponseEntity.ok(pedidos);
    }

    // Confirmar todos los pedidos de una semana para un usuario
    @PutMapping("/usuario/{idUsuario}/confirmar-semana")
    public ResponseEntity<String> confirmarSemana(
            @PathVariable Integer idUsuario,
            @RequestParam LocalDate fechaReferencia) {

        PedidoService.respuestaPeticiones respuesta = service.confirmarSemana(idUsuario, fechaReferencia);

        if (respuesta == PedidoService.respuestaPeticiones.falta_pedido) {
            return ResponseEntity.status(404).body("No hay pedidos para esa semana");
        }

        return ResponseEntity.ok("Se confirmaron los pedidos de la semana correctamente");
    }

    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarPedido(@Valid @RequestBody ConfirmarPedidoDTO dto,
            BindingResult resultado) {

        if (resultado.hasErrors()) {
            String errores = resultado.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(" , "));
            return ResponseEntity.badRequest().body(errores);
        }

        PedidoService.respuestaPeticiones respuesta = service.confirmarPedido(dto);

        if (respuesta == PedidoService.respuestaPeticiones.repetido_pedido) {
            return ResponseEntity.badRequest().body("Ya existe un pedido para ese usuario en esa fecha");
        }
        if (respuesta == PedidoService.respuestaPeticiones.sin_stock) {
            return ResponseEntity.badRequest().body("No hay stock disponible para este plato");
        }
        if (respuesta == PedidoService.respuestaPeticiones.ok) {
            return ResponseEntity.ok("Pedido creado correctamente");
        }
        return ResponseEntity.badRequest().body("No se pudo crear el pedido");
    }

    // PedidoController.java
    @PatchMapping("/confirmar-semana")
    public ResponseEntity<?> confirmarSemanaRestaurante(
            @RequestParam LocalDate fechaReferencia,
            @RequestParam(defaultValue = "0") int offset) {
        PedidoService.respuestaPeticiones respuesta = service.confirmarSemanaRestaurante(fechaReferencia, offset);

        if (respuesta == PedidoService.respuestaPeticiones.falta_pedido) {
            return ResponseEntity.status(404).body("No hay pedidos para confirmar en esa semana");
        }
        return ResponseEntity.ok("Semana confirmada");
    }

}
