package com.example.pp1.DTO.menuPlato;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModificarMenuPlatoDTO {
    
    private Integer idPlato;

    @Min(value=1, message="El stock debe ser aunque sea 1")
    private int stock;

}
