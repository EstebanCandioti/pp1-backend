package com.example.pp1.DTO.usuario;

import java.util.List;

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
    @NotBlank(message = "El nombre no puede quedar en blanco")
    private String nombre;

    @NotNull(message= "El apellido no puede quedar vacio")
    @NotBlank(message = "El apellido no puede quedar en blanco")
    private String apellido;

    @NotNull(message= "El email no puede quedar vacio")
    @NotBlank(message = "El email no puede quedar en blanco")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotNull(message= "La password no puede quedar vacia")
    @NotBlank(message = "La password no puede quedar en blanco")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message= "El no telefono puede quedar vacio")
    @NotBlank(message = "El teléfono no puede quedar en blanco")
    private String telefono;

    @NotNull(message= "La direccion no puede quedar vacia")
    @NotBlank(message = "La dirección no puede quedar en blanco")
    private String direccion;

    @NotNull
    private Boolean usuarioRestaurante;

    @NotNull(message = "Debe indicar días de asistencia")
    @Size(min = 1, message = "Debe seleccionar al menos un día de asistencia")
    private List<String> diasAsistencia; // ["LUNES", "MIERCOLES", ...]

}
