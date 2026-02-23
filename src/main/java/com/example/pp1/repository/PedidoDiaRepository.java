package com.example.pp1.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.pp1.DTO.pedidoSemanal.PedidoSemanaItemDTO;
import com.example.pp1.Entity.PedidoDia;

public interface PedidoDiaRepository extends JpaRepository<PedidoDia, Integer> {

        // Métodos que consideran soft delete (solo activos)
        Boolean existsByPedido_IdPedidoAndActivoTrue(Integer idPedido);

        List<PedidoDia> findByPedido_IdPedidoAndActivoTrue(Integer idPedido);

        List<PedidoDia> findByPedido_Usuario_IdUsuarioAndFechaEntregaBetweenAndActivoTrue(
                        Integer idUsuario,
                        LocalDate inicio,
                        LocalDate fin);

        List<PedidoDia> findByMenuDia_IdMenuDiaAndActivoTrue(Integer idMenuDia);

        Optional<PedidoDia> findByPedido_IdPedidoAndMenuDia_IdMenuDiaAndActivoTrue(
                        Integer idPedido,
                        Integer idMenuDia);

        // Métodos que NO consideran soft delete (para historial completo)
        Boolean existsByPedido_IdPedido(Integer idPedido);

        List<PedidoDia> findByPedido_IdPedido(Integer idPedido);

        List<PedidoDia> findByPedido_Usuario_IdUsuarioAndFechaEntregaBetween(
                        Integer idUsuario,
                        LocalDate inicio,
                        LocalDate fin);

        List<PedidoDia> findByMenuDia_IdMenuDia(Integer idMenuDia);

        Optional<PedidoDia> findByPedido_IdPedidoAndMenuDia_IdMenuDia(
                        Integer idPedido,
                        Integer idMenuDia);

        // Query personalizada para semana - SOLO ACTIVOS
        @Query("""
                        select new com.example.pp1.DTO.pedidoSemanal.PedidoSemanaItemDTO(
                            p.idPedido,
                            p.fechaPedido,
                            p.estado,
                            p.cantidad_personas,

                            u.idUsuario,
                            u.nombre,
                            u.apellido,

                            pd.id_pedido_dia,
                            pd.fechaEntrega,

                            pl.id_plato,
                            pl.nombre,

                            md.idMenuDia
                        )
                        from PedidoDia pd
                        join pd.pedido p
                        join p.usuario u
                        join pd.plato pl
                        join pd.menuDia md
                        where p.fechaPedido between :fechaInicio and :fechaFin
                        and pd.activo = true
                        order by p.fechaPedido asc, u.apellido asc, u.nombre asc
                        """)
        List<PedidoSemanaItemDTO> listarPedidosSemana(
                        @Param("fechaInicio") LocalDate fechaInicio,
                        @Param("fechaFin") LocalDate fechaFin);

}