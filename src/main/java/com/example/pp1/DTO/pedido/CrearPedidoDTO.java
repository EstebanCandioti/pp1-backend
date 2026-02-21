package com.example.pp1.DTO.pedido;

import java.time.LocalDate;

import com.example.pp1.Entity.Pedido.EstadosPedidos;
import com.example.pp1.Entity.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CrearPedidoDTO {
     @Column(name="fecha_pedido")
     @NotNull(message="Debe indicar la fecha del pedido")
    private LocalDate fecha_pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @NotNull(message="El pedido debe tener un estado base")
    private EstadosPedidos estado;

    @Column(name="cantidad_personas")
    @NotNull(message="debe indicar para cuantas personas es un pedido")
    private Integer cantidad_personas;

    @ManyToOne(optional=false)
    @JoinColumn(name="id_usuario", nullable=false)
    @NotNull(message="el usuario no puede ser null")
    private Usuario usuario;
}
