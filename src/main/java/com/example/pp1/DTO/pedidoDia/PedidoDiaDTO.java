package com.example.pp1.DTO.pedidoDia;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PedidoDiaDTO {
    private Integer idPedidoDia;
    private LocalDate fechaEntrega;
    private Integer idPedido;
    private Integer idMenuDia;
    private Integer idPlato;
    private String nombrePlato;
    private Boolean activo;  // Nuevo campo para saber si está cancelado
}