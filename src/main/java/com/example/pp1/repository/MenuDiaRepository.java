package com.example.pp1.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pp1.Entity.MenuDia;

public interface  MenuDiaRepository extends JpaRepository<MenuDia, Integer>{
    
    List<MenuDia> findByFecha(LocalDate fecha);
    
    List<MenuDia> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
