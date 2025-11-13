package com.example.pp1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pp1.Entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
}
