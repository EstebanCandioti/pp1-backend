package com.example.pp1.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pp1.DTO.usuario.ActualizarUsuarioDTO;
import com.example.pp1.DTO.usuario.RegistrarUsuarioDTO;
import com.example.pp1.Entity.Usuario;
import com.example.pp1.Service.UsuarioService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public List<Usuario> getUsuarios() {
        return service.obtenerUsuarios();
    }

    @PostMapping
    public ResponseEntity<?> actualizarUsuario(@Valid @RequestBody ActualizarUsuarioDTO usuario, BindingResult resultado) {
        if(resultado.hasErrors()){
            String errores = resultado.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(" , "));
            return ResponseEntity.badRequest().body(errores);
        }
        UsuarioService.resultadoPeticiones respuesta = service.actualizarUsuario(usuario);
        if(respuesta==UsuarioService.resultadoPeticiones.ok){
            return ResponseEntity.ok("El usuario se a actualizado  correctamente");
        }
        return ResponseEntity.status(400).body("No se ha encontrado al usuario");
    }


    
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@Valid @RequestBody RegistrarUsuarioDTO usuario, BindingResult resultado) {
        if(resultado.hasErrors()){
            /*GetFieldErrors devuelve un array con los campos que tienen errores, comienza un stream para mapear los datos y los une en una sola respuesta */
            String errores= resultado.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(" , "));
            return ResponseEntity.badRequest().body(errores);
        }

        UsuarioService.resultadoPeticiones respuesta= service.registrarUsuario(usuario);
        if(respuesta==UsuarioService.resultadoPeticiones.ok){
            return ResponseEntity.ok("El usuario se registro correctamente");
        }else{
            return ResponseEntity.badRequest().body("El usuario ya se encuentra registrado");
        }
    }

    @PutMapping("/estado/{id}")
    public ResponseEntity<String> estadoUsuario(@PathVariable Integer id) {
        UsuarioService.resultadoPeticiones respuesta = service.estadoUsuario(id);
        if(respuesta== UsuarioService.resultadoPeticiones.usuario_activado){
            return ResponseEntity.status(200).body("El usuario fue activado");
        }else if (respuesta==UsuarioService.resultadoPeticiones.usuario_desactivado){
            return ResponseEntity.status(200).body("El usuario fue desactivado");
        }
        return ResponseEntity.badRequest().body("No se encontro el usuario");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Integer id) {
        Optional<Usuario> user= service.obtenerUsuario(id);
        if(user.isPresent()){
            return ResponseEntity.ok(user.get());
        }else{
            return ResponseEntity.status(404).body("No se encontro el usuario");
        }
    }

    record Login(String email, String password){};

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login body) {
        UsuarioService.resultadoPeticiones respuesta = service.login(body.email(), body.password());
        if(respuesta== UsuarioService.resultadoPeticiones.logeado){
            Optional<Usuario> user = service.obtenerUsuarioPorEmail(body.email());
            return ResponseEntity.ok(user.get());
        }
        if( respuesta== UsuarioService.resultadoPeticiones.password_incorrecta){
            return ResponseEntity.badRequest().body("Contrasenia incorrecta");
        }
        
        return ResponseEntity.status(404).body("No se encontro un usuario con este email");
    }
    
}