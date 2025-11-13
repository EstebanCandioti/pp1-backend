package com.example.pp1.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActualizarUsuarioDTO {
    
    private Integer id;
    
    @NotNull(message="El nombre no puede quedar vacio")
    @NotBlank(message = "El nombre no puede quedar vacio")
    private String nombre;

    @NotNull(message="El apellido no puede quedar vacio")
    @NotBlank(message = "El apellido no puede quedar vacio")
    private String apellido;

    @NotNull(message="El telefono no puede quedar vacio")
    @NotBlank(message = "El telefono no puede quedar vacio")
    private String telefono;

    @NotNull(message="La direccion no puede quedar vacia")
    @NotBlank(message = "La dirección no puede quedar vacia")
    private String direccion;
}
