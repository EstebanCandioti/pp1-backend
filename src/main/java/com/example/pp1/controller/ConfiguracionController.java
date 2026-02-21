package com.example.pp1.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pp1.Entity.Configuracion;
import com.example.pp1.Service.ConfiguracionService;

@RestController
@RequestMapping("/configuracion")
public class ConfiguracionController {

    @Autowired
    private ConfiguracionService service;

    /**
     * GET /configuracion
     * Devuelve la configuración actual del sistema.
     */
    @GetMapping
    public ResponseEntity<Configuracion> obtenerConfiguracion() {
        Configuracion config = service.obtenerConfiguracion();
        return ResponseEntity.ok(config);
    }

    /**
     * PATCH /configuracion/horario-limite
     * Actualiza el horario límite para realizar pedidos.
     */
    @PatchMapping("/horario-limite")
    public ResponseEntity<String> actualizarHorarioLimite(@RequestParam String horarioLimite) {
        ConfiguracionService.RespuestaPeticiones respuesta = service.actualizarHorarioLimite(horarioLimite);

        if (respuesta == ConfiguracionService.RespuestaPeticiones.horario_invalido) {
            return ResponseEntity.badRequest().body("El horario ingresado no es válido. Use el formato HH:MM (ej: 10:30)");
        }
        return ResponseEntity.ok("Horario límite actualizado correctamente");
    }

    /**
     * PUT /configuracion/feriados
     * Reemplaza toda la lista de feriados.
     */
    @PutMapping("/feriados")
    public ResponseEntity<String> actualizarFeriados(@RequestBody List<LocalDate> feriados) {
        ConfiguracionService.RespuestaPeticiones respuesta = service.actualizarFeriados(feriados);

        if (respuesta == ConfiguracionService.RespuestaPeticiones.fecha_invalida) {
            return ResponseEntity.badRequest().body("La lista de feriados no puede ser nula");
        }
        return ResponseEntity.ok("Lista de feriados actualizada correctamente");
    }

    /**
     * POST /configuracion/feriado
     * Agrega un feriado a la lista.
     */
    @PostMapping("/feriado")
    public ResponseEntity<String> agregarFeriado(@RequestParam LocalDate fecha) {
        ConfiguracionService.RespuestaPeticiones respuesta = service.agregarFeriado(fecha);

        if (respuesta == ConfiguracionService.RespuestaPeticiones.fecha_invalida) {
            return ResponseEntity.badRequest().body("La fecha ingresada no es válida");
        }
        return ResponseEntity.ok("Feriado agregado correctamente");
    }

    /**
     * DELETE /configuracion/feriado
     * Elimina un feriado de la lista.
     */
    @DeleteMapping("/feriado")
    public ResponseEntity<String> eliminarFeriado(@RequestParam LocalDate fecha) {
        ConfiguracionService.RespuestaPeticiones respuesta = service.eliminarFeriado(fecha);

        if (respuesta == ConfiguracionService.RespuestaPeticiones.fecha_invalida) {
            return ResponseEntity.badRequest().body("La fecha ingresada no es válida");
        }
        return ResponseEntity.ok("Feriado eliminado correctamente");
    }
}