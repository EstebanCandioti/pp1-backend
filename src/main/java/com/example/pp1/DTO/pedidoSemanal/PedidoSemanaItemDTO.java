package com.example.pp1.DTO.pedidoSemanal;

import java.time.LocalDate;

import com.example.pp1.Entity.Pedido;

public record PedidoSemanaItemDTO(
  Integer idPedido,
  LocalDate fechaPedido,
  Pedido.EstadosPedidos estado,
  Integer cantidadPersonas,

  Integer idUsuario,
  String nombreUsuario,
  String apellidoUsuario,

  Integer idPedidoDia,
  LocalDate fechaEntrega,

  Integer idPlato,
  String nombrePlato,

  Integer idMenuDia
) {}
