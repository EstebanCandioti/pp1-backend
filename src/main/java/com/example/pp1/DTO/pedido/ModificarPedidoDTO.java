package com.example.pp1.DTO.pedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ModificarPedidoDTO {
    
    @NotNull(message = "No puede ser vacio el id del pedido")
    private Integer id;

    @NotNull(message = "No puede ser vacio la cantidad de personas")
    @Min(value = 1, message = "Se debe pedir para aunque sea 1 persona")
    private Integer cantidad_personas;
}
