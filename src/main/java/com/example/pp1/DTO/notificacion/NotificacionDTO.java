package com.example.pp1.DTO.notificacion;


import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificacionDTO {

    private Integer idNotificacion;
    private Date fechaEnvio;
    private String asunto;
    private String mensaje;
    private Boolean leida;
    private Integer idUsuario;
    private String nombreUsuario; // opcional, pero útil para debug o vistas
}