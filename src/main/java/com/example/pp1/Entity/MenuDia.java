package com.example.pp1.Entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="MenuDia")
public class MenuDia {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_menu_dia")
    private Integer id_menu_dia;

    @Column(name="fecha")
    private LocalDate fecha;

    @Column(name="descripcion")
    private String descripcion;

    @Column(name="publicado")
    private Boolean publicado;

    @Column(name="stock_total")
    private Integer stock_total;

    @ManyToOne
    @JoinColumn(name="id_usuario", nullable=false)
    private Usuario usuario;
}
