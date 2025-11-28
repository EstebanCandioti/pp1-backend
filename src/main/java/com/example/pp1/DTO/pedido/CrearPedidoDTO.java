package com.example.pp1.DTO.pedido;

import java.time.LocalDate;

import com.example.pp1.Entity.Pedido.EstadosPedidos;
import com.example.pp1.Entity.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CrearPedidoDTO {
     @Column(name="fecha_pedido")
    private LocalDate fecha_pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadosPedidos estado;

    @Column(name="cantidad_personas")
    private Integer cantidad_personas;

    @ManyToOne(optional=false)
    @JoinColumn(name="id_usuario", nullable=false)
    private Usuario usuario;
}
