package com.example.pp1.DTO.pedidoDia;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrearPedidoDiaDTO{
    
    @NotNull(message = "El id del pedido no puede quedar vacío")
    private Integer idPedido;

    @NotNull(message = "El id del menú del día no puede quedar vacío")
    private Integer idMenuDia;

    @NotNull(message = "El id del plato no puede quedar vacío")
    private Integer idPlato;

    @NotNull(message = "La fecha de entrega no puede quedar vacía")
    private LocalDate fechaEntrega;
}
