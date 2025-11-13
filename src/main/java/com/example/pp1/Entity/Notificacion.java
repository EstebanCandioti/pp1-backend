package com.example.pp1.Entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer id_notificacion;

    @Column(name="fecha_envio")
    private Date fecha_envio;

    @Column(name="asunto")
    private String asunto;
    
    @Column(name="mensaje")
    private String mensaje;

    @ManyToOne
    @JoinColumn(name="id_usuario", nullable=false)
    private Usuario usuario;
}
