package com.example.pp1.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.pp1.DTO.plato.ActualizarPlatoDTO;
import com.example.pp1.DTO.plato.RegistrarPlatoDTO;
import com.example.pp1.Entity.Plato;
import com.example.pp1.Service.PlatoService;

import jakarta.validation.Valid;




@RestController
@RequestMapping("/plato")
public class PlatoController {
    
    @Autowired
    private PlatoService service;

    @GetMapping
    public ResponseEntity<?> traerPlatos() {
        List<Plato> platos = service.traerPlatos();
        if(platos.isEmpty()){
            return ResponseEntity.status(404).body("No se encontraron platos");
        }
        return ResponseEntity.ok(platos);
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearPlato(@Valid @RequestBody RegistrarPlatoDTO platoDTO, BindingResult resultado) {
        if(resultado.hasErrors()){
            String errores = resultado.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(" , "));
            return ResponseEntity.badRequest().body(errores);
        }
        PlatoService.respuestasSolicitudes respuesta = service.crearPlato(platoDTO);
        if(respuesta==PlatoService.respuestasSolicitudes.plato_creado){
            return ResponseEntity.ok("Se ha creado el plato");
        }
        
        return ResponseEntity.status(401).body("Hubo un error creando el plato");
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<String> modificarPlato(@PathVariable Integer id, @Valid @RequestBody ActualizarPlatoDTO platoDTO, BindingResult resultado) {
        if(resultado.hasErrors()){
            String errores = resultado.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(" , "));
            return ResponseEntity.badRequest().body(errores);
        }
        PlatoService.respuestasSolicitudes respuesta = service.modificarPlato(platoDTO);
        if(respuesta == PlatoService.respuestasSolicitudes.falta_plato){
            return ResponseEntity.status(404).body("No se a encontrado el plato a modificar");
        }
        return ResponseEntity.ok("El plato se a modificado correctamente");
    }
    
    @DeleteMapping("/estado/{id}")
    public ResponseEntity<String> cambiarEstadoPlato(@PathVariable Integer id){
        PlatoService.respuestasSolicitudes respuesta= service.cambiarEstadoPlato(id);
        if(respuesta== PlatoService.respuestasSolicitudes.falta_plato){
            return ResponseEntity.status(404).body("No se a encontrado el plato a borrar");
        }
        return ResponseEntity.ok("Se ha borrado el plato");
    }
}
