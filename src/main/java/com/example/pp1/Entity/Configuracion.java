package com.example.pp1.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "configuracion")
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Integer idConfiguracion;

    @Column(name = "horario_limite", nullable = false)
    private String horarioLimite;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "configuracion_feriados",
        joinColumns = @JoinColumn(name = "id_configuracion")
    )
    @Column(name = "fecha_feriado")
    private List<LocalDate> feriados = new ArrayList<>();

    public Configuracion(String horarioLimite) {
        this.horarioLimite = horarioLimite;
    }

}