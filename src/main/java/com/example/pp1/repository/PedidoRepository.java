package com.example.pp1.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pp1.Entity.Pedido;
import com.example.pp1.Entity.Usuario;

public interface  PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByUsuario_IdUsuario(Integer idUsuario);
    
    List<Pedido> findByUsuario_IdUsuarioAndEstado(Integer idUsuario, Pedido.EstadosPedidos estado);

    boolean existsByUsuarioAndFechaPedido(Usuario usuario, LocalDate fechaPedido);

    List<Pedido> findByUsuario_IdUsuarioAndFechaPedidoBetween(Integer idUsuario, LocalDate inicio, LocalDate fin);
}
