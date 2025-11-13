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

@NoArgsConstructor
@Getter
@Setter
@Entity
public class PedidoDia {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_pedido_dia")
    private Integer id_pedido_dia;

    @Column(name="fecha_entrega")
    private Date fecha_entrega;

    @ManyToOne
    @JoinColumn(name="id_pedido", nullable=false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name="id_menu_dia", nullable=false)
    private MenuDia menu_dia;
    
    @ManyToOne
    @JoinColumn(name="id_plato", nullable=false)
    private Plato plato;
}
