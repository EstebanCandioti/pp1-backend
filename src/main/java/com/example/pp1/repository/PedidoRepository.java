package com.example.pp1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.pp1.Entity.Pedido;

public interface  PedidoRepository extends JpaRepository<Pedido, Integer> {
    @Query("SELECT p from Pedido p WHERE p.usuario.id_usuario=:id_usuario")
    List<Pedido> findById_usuario(Integer id_usuario);
}
