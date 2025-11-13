package com.example.pp1.Entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id_pedido")
    private Integer id_pedido;

    @Column(name="fecha_pedido")
    private Date fecha_pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadosPedidos estado;

    @Column(name="cantidad_personas")
    private Integer cantidad_personas;

    @ManyToOne(optional=false)
    @JoinColumn(name="id_usuario", nullable=false)
    private Usuario usuario;

    public enum EstadosPedidos {Pendiente, Confirmado, Cancelado}
}
