package com.example.pp1.Entity;

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
@Setter
@Getter
@Entity
public class MenuPlato {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_menu_plato")
    private Integer id_menu_plato;

    @ManyToOne
    @JoinColumn(name="id_plato", nullable=false)
    private Plato plato;

    @ManyToOne
    @JoinColumn(name="id_menu_dia", nullable=false)
    private MenuDia menu_dia;

    @Column(name="stock_disponible")
    private int stock_disponible;
}
