package com.example.pp1.DTO;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrearMenuDiaDTO {

    private LocalDate fecha;

    private String descripcion;

    private Boolean publicado;

    private Integer id_usuario;

    private Integer stock_total;
}
