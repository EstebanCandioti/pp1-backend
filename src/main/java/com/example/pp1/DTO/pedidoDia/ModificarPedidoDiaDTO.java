package com.example.pp1.DTO.pedidoDia;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModificarPedidoDiaDTO {
    @NotNull(message = "El id del detalle de pedido es obligatorio")
    private Integer idPedidoDia;

    @NotNull(message = "El id del plato es obligatorio")
    private Integer idPlatoNuevo;

    @NotNull(message = "El id del menú del día es obligatorio")
    private Integer idMenuDiaNuevo;
}
