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

import com.example.pp1.DTO.menuPlato.CrearMenuPlatoDTO;
import com.example.pp1.DTO.menuPlato.ModificarMenuPlatoDTO;
import com.example.pp1.Entity.MenuPlato;
import com.example.pp1.Service.MenuPlatoService;

import jakarta.validation.Valid;




@RestController
@RequestMapping("/menu-plato")
public class MenuPlatoController {
    @Autowired
    private MenuPlatoService service;

    @GetMapping("/fecha")
    public ResponseEntity<?> traerMenuPlatosFecha(@RequestParam LocalDate fecha) {
        List<MenuPlato> platos = service.obtenerPlatosPorFecha(fecha);
        if(platos.isEmpty()){
            return ResponseEntity.status(404).body("No se han encontrado platos para esta fecha");
        }
        return ResponseEntity.ok(platos);
    }

    @GetMapping("/menu-dia/{idMenuDia}")
    public ResponseEntity<?> traerMenuPlatosMenuDia(@PathVariable Integer idMenuDia) {
        try {
            List<MenuPlato> platos= service.obtenerPlatosPorMenuDia(idMenuDia);
             if(platos.isEmpty()){
            return ResponseEntity.status(404).body("No se encontraron platos para este menu dia");
        }
        return ResponseEntity.ok(platos);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/plato")
    public ResponseEntity<String> agregarPlato(@Valid @RequestBody CrearMenuPlatoDTO menuDTO, BindingResult resultado) {
        if(resultado.hasErrors()){
            String errores = resultado.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(" , "));
            return ResponseEntity.badRequest().body(errores);
        }
        MenuPlatoService.respuestaPeticiones respuesta = service.agregarPlato(menuDTO);
        if(respuesta==MenuPlatoService.respuestaPeticiones.falta_menuDia){
            return ResponseEntity.status(404).body("No se ha encontrado el menu para agregar los platos");
        }
        if(respuesta==MenuPlatoService.respuestaPeticiones.falta_plato){
            return ResponseEntity.status(404).body("No se ha encontrado el plato que se quiere agregar");
        }

        if(respuesta==MenuPlatoService.respuestaPeticiones.duplicado_menu){
            return ResponseEntity.badRequest().body("Ya existe este menu");
        }

        return ResponseEntity.ok("Se ha agregar el plato correctamente");
    }
    
    @DeleteMapping("/plato/{id}")
    public ResponseEntity<String> borrarPlato(@PathVariable Integer id){
        MenuPlatoService.respuestaPeticiones respuesta = service.eliminarPlatoMenuDia(id);
        if(respuesta==MenuPlatoService.respuestaPeticiones.falta_menuPlato){
            return ResponseEntity.status(404).body("No se ha encontrado el menu para borrar el plato");
        }
        return ResponseEntity.ok("Se ha borrado el plato");
    }

    @GetMapping()
    public ResponseEntity<?> traerTodosMenuPlato() {
        List<MenuPlato> platos = service.traerTodos();
        if(platos.isEmpty()){
            return ResponseEntity.status(404).body("No se han encontrado platos");
        }
        return ResponseEntity.ok(platos);
    }

    @PutMapping("modificar/{id}")
    public ResponseEntity<String> modificarPlato(@PathVariable Integer id,@Valid @RequestBody ModificarMenuPlatoDTO menuDTO, BindingResult resultado) {
        if(resultado.hasErrors()){
            String errores = resultado.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(" , "));
            return ResponseEntity.badRequest().body(errores);
        }
        
        MenuPlatoService.respuestaPeticiones respuesta =service.modificarMenuPlato(id, menuDTO);
        if(respuesta== MenuPlatoService.respuestaPeticiones.falta_menuPlato){
            return ResponseEntity.status(404).body("No se ha encontrado el plato del menu que se quiere modificar");
        }

        if(respuesta==MenuPlatoService.respuestaPeticiones.falta_plato){
            return ResponseEntity.status(404).body("No se ha encontrado el plato que se quiere agregar");
        }

        return ResponseEntity.ok("Se he modificado el plato");
    }
    
}
