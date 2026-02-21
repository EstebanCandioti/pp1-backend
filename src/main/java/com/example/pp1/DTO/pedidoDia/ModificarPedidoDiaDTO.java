package com.example.pp1.DTO.pedidoDia;

import jakarta.validation.constraints.Min;
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

    @NotNull(message="Se debe seleccionar la cantidad  de personas para el pedido")
    @Min(value=1, message="Debe seleccionar minimamente una persona para el pedido")
    private Integer cantidadPersonas;
}
