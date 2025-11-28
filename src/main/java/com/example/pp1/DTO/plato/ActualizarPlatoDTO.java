package com.example.pp1.DTO.plato;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActualizarPlatoDTO {

    @Id
    @NotNull(message="El id no puede ser null")
    public Integer id_plato;
    
    @NotNull(message= "El nombre no puede quedar vacio")
    @NotBlank(message = "El nombre no puede quedar vacío")
    public String nombre;

    @NotNull(message= "La descripcion no puede quedar vacia")
    @NotBlank(message = "La descripcion no puede quedar vacia")
    public String descripcion;

    
    @NotNull(message= "La imagen no puede quedar vacia")
    @NotBlank(message = "La imagen no puede quedar vacia")
    public String imagen;

    @NotNull(message= "La categoria no puede quedar vacia")
    @NotBlank(message = "La categoria no puede quedar vacia")
    public String categoria;
}
