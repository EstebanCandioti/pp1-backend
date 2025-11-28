package com.example.pp1.DTO.menuDIa;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActualizarMenuDiaDTO {
    @NotNull(message="El menu necesita un id")
    private Integer id;

    @NotNull(message="La fecha no puede quedar vacia")
    private LocalDate fecha;

    @NotBlank(message="La descripcion no puede quedar vacia")
    @NotNull(message="La descripcion no puede quedar vacia")
    private String descripcion;

    @NotNull(message="El menu tiene que tener una cantidad de stock")
    private Integer stock_total;
}
