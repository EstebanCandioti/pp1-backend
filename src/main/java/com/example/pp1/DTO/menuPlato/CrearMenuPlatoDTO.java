package com.example.pp1.DTO.menuPlato;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrearMenuPlatoDTO {

    @NotNull(message="Falta id MenuDia")
    private Integer idMenuDia;

    @NotNull(message="Falta id Plato")
    private Integer idPlato;

    @NotNull(message="Falta stock inicial")
    @Min(value= 1, message="El stock debe ser mayor a 0")
    private Integer stockInicial;
}
