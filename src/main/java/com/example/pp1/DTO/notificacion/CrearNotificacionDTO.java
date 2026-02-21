package com.example.pp1.DTO.notificacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrearNotificacionDTO {

    @NotBlank(message = "El asunto no puede quedar vacío")
    private String asunto;

    @NotBlank(message = "El mensaje no puede quedar vacío")
    private String mensaje;

    @NotNull(message = "El id del usuario no puede quedar vacío")
    private Integer idUsuario;
}