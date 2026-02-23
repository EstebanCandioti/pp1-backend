package com.example.pp1.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pp1.DTO.pedidoDia.CrearPedidoDiaDTO;
import com.example.pp1.DTO.pedidoDia.ModificarPedidoDiaDTO;
import com.example.pp1.DTO.pedidoDia.PedidoDiaDTO;
import com.example.pp1.DTO.pedidoSemanal.PedidoSemanaItemDTO;
import com.example.pp1.Service.PedidoDiaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pedido-dia")
public class PedidoDiaController {

    @Autowired
    private PedidoDiaService service;

    // Crear un nuevo PedidoDia
    @PostMapping
    public ResponseEntity<String> crearPedidoDia(
            @Valid @RequestBody CrearPedidoDiaDTO dto,
            BindingResult resultado) {

        if (resultado.hasErrors()) {
            String errores = resultado.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(" , "));
            return ResponseEntity.badRequest().body(errores);
        }

        PedidoDiaService.RespuestaPeticiones respuesta = service.crearPedidoDia(dto);

        switch (respuesta) {
            case falta_pedido:
                return ResponseEntity.status(404).body("No se encontró el pedido");

            case falta_menu_dia:
                return ResponseEntity.status(404).body("No se encontró el menú del día");

            case falta_plato:
                return ResponseEntity.status(404).body("No se encontró el plato indicado");

            case falta_menu_plato:
                return ResponseEntity.status(404).body("No se encontró la relación menú–plato para ese día");

            case pedido_no_editable:
                return ResponseEntity.badRequest()
                        .body("No se puede agregar un día a un pedido confirmado");

            case stock_insuficiente:
                return ResponseEntity.badRequest()
                        .body("No hay stock suficiente para la cantidad de personas del pedido");

            case ok:
                return ResponseEntity.ok("El pedido del día se creó correctamente");

            default:
                return ResponseEntity.badRequest().body("No se pudo crear el pedido del día");
        }
    }

    /**
     * Listar PedidoDia ACTIVOS por idPedido
     * Usado por "Mi Pedido" - Solo muestra pedidos modificables
     */
    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<?> listarPorPedido(@PathVariable Integer idPedido) {

        List<PedidoDiaDTO> lista = service.listarPorPedido(idPedido);

        if (lista.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No se encontraron registros de pedido del día para este pedido");
        }

        return ResponseEntity.ok(lista);
    }

    /**
     * Listar TODOS los PedidoDia por idPedido (incluyendo cancelados)
     * Usado por "Historial de Pedidos" - Muestra el historial completo
     */
    @GetMapping("/pedido/{idPedido}/historial")
    public ResponseEntity<?> listarPorPedidoCompleto(@PathVariable Integer idPedido) {

        List<PedidoDiaDTO> lista = service.listarPorPedidoCompleto(idPedido);

        if (lista.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No se encontraron registros para este pedido");
        }

        return ResponseEntity.ok(lista);
    }

    // Eliminar un PedidoDia (soft delete - marca como inactivo)
    @DeleteMapping("/{idPedidoDia}")
    public ResponseEntity<String> eliminarPedidoDia(@PathVariable Integer idPedidoDia) {

        PedidoDiaService.RespuestaPeticiones respuesta = service.eliminarPedidoDia(idPedidoDia);

        switch (respuesta) {
            case falta_pedido_dia:
                return ResponseEntity.status(404).body("No se encontró el registro de pedido del día");

            case falta_menu_plato:
                return ResponseEntity.status(404)
                        .body("No se encontró el menú–plato asociado para reponer el stock");

            case ok:
                return ResponseEntity.ok("El pedido del día se eliminó correctamente");

            case pedido_no_editable:
                return ResponseEntity.status(401).body("No se puede eliminar un pedido confirmado");

            case falta_pedido:
                return ResponseEntity.status(404).body("No se encontró el pedido correspondiente al pedidoDia");
            default:
                return ResponseEntity.badRequest().body("No se pudo eliminar el pedido del día");
        }
    }

    // Modificar un PedidoDia (cambiar menú/plato del día)
    @PutMapping
    public ResponseEntity<String> modificarPedidoDia(
            @Valid @RequestBody ModificarPedidoDiaDTO dto,
            BindingResult resultado) {

        if (resultado.hasErrors()) {
            String errores = resultado.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(" , "));
            return ResponseEntity.badRequest().body(errores);
        }

        PedidoDiaService.RespuestaPeticiones respuesta = service.modificarPedidoDia(dto);

        switch (respuesta) {
            case falta_pedido_dia:
                return ResponseEntity.status(404).body("No se encontró el registro de pedido del día");

            case falta_menu_dia:
                return ResponseEntity.status(404).body("No se encontró el nuevo menú del día");

            case falta_plato:
                return ResponseEntity.status(404).body("No se encontró el nuevo plato indicado");

            case falta_menu_plato:
                return ResponseEntity.status(404)
                        .body("No se encontró la relación menú–plato para el menú y plato nuevos");

            case pedido_no_editable:
                return ResponseEntity.badRequest()
                        .body("No se puede modificar un pedido del día porque el pedido está confirmado");

            case stock_insuficiente:
                return ResponseEntity.badRequest()
                        .body("No hay stock suficiente para el nuevo plato seleccionado");

            case ok:
                return ResponseEntity.ok("El pedido del día se modificó correctamente");

            default:
                return ResponseEntity.badRequest().body("No se pudo modificar el pedido del día");
        }
    }

    @GetMapping("/semana")
    public ResponseEntity<?> listarPedidosSemana(
            @RequestParam LocalDate fechaReferencia,
            @RequestParam(defaultValue = "0") int offset) {
        List<PedidoSemanaItemDTO> lista = service.listarPedidosSemana(fechaReferencia, offset);
        if (lista.isEmpty())
            return ResponseEntity.status(404).body("No hay pedidos para esa semana");
        return ResponseEntity.ok(lista);
    }
}