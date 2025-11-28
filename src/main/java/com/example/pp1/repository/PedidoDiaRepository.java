package com.example.pp1.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pp1.Entity.PedidoDia;

public interface PedidoDiaRepository extends JpaRepository<PedidoDia, Integer>{
    
    List<PedidoDia> findByPedido_IdPedido(Integer idPedido);

    List<PedidoDia> findByPedido_Usuario_IdUsuarioAndFechaEntregaBetween(
            Integer idUsuario,
            LocalDate inicio,
            LocalDate fin
    );

    List<PedidoDia> findByMenuDia_IdMenuDia(Integer idMenuDia);

    Optional<PedidoDia> findByPedido_IdPedidoAndMenuDia_IdMenuDia(
            Integer idPedido,
            Integer idMenuDia
    );

}