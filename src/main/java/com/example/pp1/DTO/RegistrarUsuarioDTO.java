package com.example.pp1.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistrarUsuarioDTO {

    @NotNull(message= "El nombre no puede quedar vacio")
    @NotBlank(message = "El nombre no puede quedar vacío")
    private String nombre;

    @NotNull(message= "El apellido no puede quedar vacio")
    @NotBlank(message = "El apellido no puede quedar vacío")
    private String apellido;

    @NotNull(message= "El email no puede quedar vacio")
    @NotBlank(message = "El email no puede quedar vacío")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotNull(message= "La password no puede quedar vacia")
    @NotBlank(message = "La contraseña no puede quedar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message= "El no telefono puede quedar vacio")
    @NotBlank(message = "El teléfono no puede quedar vacío")
    private String telefono;

    @NotNull(message= "La direccion no puede quedar vacia")
    @NotBlank(message = "La dirección no puede quedar vacía")
    private String direccion;

    @NotNull
    private Boolean usuarioRestaurante;
}
