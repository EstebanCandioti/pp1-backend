package com.example.pp1.DTO;

import com.example.pp1.Entity.Usuario;

public record LoginResponse(String token, Usuario usuario) {
    
}
