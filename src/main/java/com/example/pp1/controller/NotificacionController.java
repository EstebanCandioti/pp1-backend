package com.example.pp1.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pp1.DTO.notificacion.CrearNotificacionDTO;
import com.example.pp1.DTO.notificacion.NotificacionDTO;
import com.example.pp1.Service.NotificacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService service;

    // 1 Listar notificaciones de un usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> obtenerNotificacionesUsuario(@PathVariable Integer idUsuario) {

        List<NotificacionDTO> lista = service.obtenerNotificacionesUsuario(idUsuario);

        if (lista.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No se encontraron notificaciones para este usuario");
        }

        return ResponseEntity.ok(lista);
    }

    // 2 Crear notificación manual (ej: admin o debug)
    @PostMapping
    public ResponseEntity<String> crearNotificacion(
            @Valid @RequestBody CrearNotificacionDTO dto,
            BindingResult resultado) {

        if (resultado.hasErrors()) {
            String errores = resultado.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(" , "));
            return ResponseEntity.badRequest().body(errores);
        }

        NotificacionService.RespuestaPeticiones respuesta = service.crearNotificacion(dto);

        switch (respuesta) {
            case falta_usuario:
                return ResponseEntity.status(404)
                        .body("No se encontró el usuario para asociar la notificación");

            case ok:
                return ResponseEntity.ok("La notificación fue creada correctamente");

            default:
                return ResponseEntity.badRequest().body("No se pudo crear la notificación");
        }
    }

    // 3 Eliminar todas las notificaciones de un usuario
    @DeleteMapping("/usuario/{idUsuario}")
    public ResponseEntity<String> eliminarNotificacionesUsuario(@PathVariable Integer idUsuario) {

        NotificacionService.RespuestaPeticiones respuesta = service.eliminarNotificacionesUsuario(idUsuario);

        switch (respuesta) {
            case falta_usuario:
                return ResponseEntity.status(404)
                        .body("No se encontró el usuario");

            case ok:
                return ResponseEntity.ok("Se eliminaron todas las notificaciones del usuario");

            default:
                return ResponseEntity.badRequest().body("No se pudieron eliminar las notificaciones");
        }
    }

    // 4 Marcar una notificación específica como leída
    @PatchMapping("/{idNotificacion}/marcar-leida")
    public ResponseEntity<String> marcarComoLeida(@PathVariable Integer idNotificacion) {

        NotificacionService.RespuestaPeticiones respuesta = service.marcarComoLeida(idNotificacion);

        switch (respuesta) {
            case falta_notificacion:
                return ResponseEntity.status(404)
                        .body("No se encontró la notificación");

            case ok:
                return ResponseEntity.ok("Notificación marcada como leída");

            default:
                return ResponseEntity.badRequest().body("No se pudo marcar la notificación como leída");
        }
    }

    // 5 Marcar todas las notificaciones de un usuario como leídas
    @PatchMapping("/usuario/{idUsuario}/marcar-todas-leidas")
    public ResponseEntity<String> marcarTodasLeidasUsuario(@PathVariable Integer idUsuario) {

        NotificacionService.RespuestaPeticiones respuesta = service.marcarTodasLeidasUsuario(idUsuario);

        switch (respuesta) {
            case falta_usuario:
                return ResponseEntity.status(404)
                        .body("No se encontró el usuario");

            case ok:
                return ResponseEntity.ok("Todas las notificaciones fueron marcadas como leídas");

            default:
                return ResponseEntity.badRequest().body("No se pudieron marcar las notificaciones como leídas");
        }
    }
}