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

import com.example.pp1.DTO.menuDIa.ActualizarMenuDiaDTO;
import com.example.pp1.DTO.menuDIa.CrearMenuDiaDTO;
import com.example.pp1.Entity.MenuDia;
import com.example.pp1.Service.MenuDiaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("menu-dia")
public class MenuDiaController {

    @Autowired
    MenuDiaService service;

    @GetMapping
    public ResponseEntity<?> obtenerMenus() {
        List<MenuDia> menus = service.obtenerMenusDia();
        if (menus.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontraron menus");
        }
        return ResponseEntity.ok(menus);
    }

    @PatchMapping("/cambiar-estado/{id}")
    public ResponseEntity<?> cambiarEstadoMenu(@PathVariable Integer id) {
        MenuDiaService.respuestaPeticiones respuesta = service.cambiarEstadoMenuDia(id);
        if (respuesta == MenuDiaService.respuestaPeticiones.falta_menu) {
            return ResponseEntity.status(404).body("No se ha encontrado el menu");
        }
        return ResponseEntity.ok("Se ha cambiado el estado del menu");
    }

    @PutMapping("/actualizar-menu")
    public ResponseEntity<?> actualizarMenuDia(@Valid @RequestBody ActualizarMenuDiaDTO menu, BindingResult resultado) {
        if (resultado.hasErrors()) {
            String errores = resultado.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(" , "));
            return ResponseEntity.badRequest().body(errores);
        }

        MenuDiaService.respuestaPeticiones respuesta = service.actualizarMenuDia(menu);
        if (respuesta == MenuDiaService.respuestaPeticiones.falta_menu) {
            return ResponseEntity.status(404).body("No se ha encontrado el menu");
        }
        return ResponseEntity.ok("El menu se ha actualizado");
    }

    @PostMapping("/crear-menu")
    public ResponseEntity<?> crearMenuDia(@Valid @RequestBody CrearMenuDiaDTO menu, BindingResult resultado) {
        if (resultado.hasErrors()) {
            String errores = resultado.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(" , "));
            return ResponseEntity.badRequest().body(errores);
        }

        MenuDiaService.respuestaPeticiones respuesta = service.crearMenuDia(menu);

        if (respuesta == MenuDiaService.respuestaPeticiones.falta_usuario) {
            return ResponseEntity.status(404).body("No se ha encontrado el usuario creador");
        }
        return ResponseEntity.ok("Se ha creado el menu");
    }

    @GetMapping("/fecha")
    public ResponseEntity<?> obtenerMenuDiaFecha(@RequestParam LocalDate fecha) {
        List<MenuDia> menus = service.obtenerMenusDiaFecha(fecha);
        if (menus.isEmpty()) {
            return ResponseEntity.status(404).body("No hay menus cargados para este dia");
        }
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/semana")
    public ResponseEntity<?> obtenerMenusSemana(
            @RequestParam LocalDate fechaReferencia,
            @RequestParam(defaultValue = "0") int offset) {
        List<MenuDia> menus = service.obtenerMenusSemana(fechaReferencia, offset);

        if (menus.isEmpty()) {
            return ResponseEntity.status(404).body("No hay menús para esa semana");
        }

        return ResponseEntity.ok(menus);
    }

}