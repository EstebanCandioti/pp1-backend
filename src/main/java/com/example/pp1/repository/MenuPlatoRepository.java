package com.example.pp1.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pp1.Entity.MenuDia;
import com.example.pp1.Entity.MenuPlato;
import com.example.pp1.Entity.Plato;

public interface MenuPlatoRepository extends JpaRepository<MenuPlato, Integer>{
    List<MenuPlato> findByMenuDia_Fecha(LocalDate fecha);

    Boolean existsByMenuDiaAndPlato(MenuDia menuDia, Plato plato);
    
    List<MenuPlato> findByMenuDia(MenuDia menuDia);

    Optional<MenuPlato> findByMenuDiaAndPlato(MenuDia menuDia, Plato plato);
    
}
