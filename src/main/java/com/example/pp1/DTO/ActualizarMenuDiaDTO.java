package com.example.pp1.DTO;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActualizarMenuDiaDTO {
    private Integer id;

    private LocalDate fecha;

    private String descripcion;

    private Integer stock_total;
}
