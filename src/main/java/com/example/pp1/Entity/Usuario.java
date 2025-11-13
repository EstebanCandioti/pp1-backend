package com.example.pp1.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    private Integer id_usuario;

    @Column(name="nombre")
    private String nombre;

    @Column(name="apellido")
    private String apellido;

    @Email
    @Column(unique=true)
    private String correo;

    @Column(name="password")
    private String password;

    @Column(name="telefono")
    private String telefono;

    @Column(name="direccion")
    private String direccion;

    @Column(name="es_usuario_restaurante")
    private Boolean es_usuario_restaurante;

    @Column(name="activo")
    private Boolean activo;
}
