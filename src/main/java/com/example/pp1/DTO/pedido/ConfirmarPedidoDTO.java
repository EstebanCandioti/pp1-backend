package com.example.pp1.DTO.pedido;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record ConfirmarPedidoDTO(
    @NotNull(message="El usuario es obligatorio") 
    Integer idUsuario,
    
    @NotNull(message="El menú/plato es obligatorio") 
    Integer idMenuPlato,

    @NotNull(message="Debe indicar la cantidad de personas") 
    Integer cantidadPersonas,
    LocalDate fechaEntrega // opcional
) {}