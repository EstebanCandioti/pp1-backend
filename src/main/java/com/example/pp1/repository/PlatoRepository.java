package com.example.pp1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pp1.Entity.Plato;

public interface  PlatoRepository extends JpaRepository<Plato, Integer>{
    
}