package com.example.pp1.DTO.menuPlato;

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
    private Integer stockInicial;
}
