package com.example.pp1.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.pp1.DTO.pedido.CrearPedidoDTO;
import com.example.pp1.DTO.pedido.ModificarPedidoDTO;
import com.example.pp1.Entity.EstadosPedidos;
import com.example.pp1.Entity.Pedido;
import com.example.pp1.repository.PedidoRepository;

@Service
public class PedidoService {
    
    private final PedidoRepository repo;
    private final MenuPlatoService serviceMenu;

    public PedidoService(PedidoRepository repo, MenuPlatoService serviceMenu){
        this.repo=repo;
        this.serviceMenu=serviceMenu;
    }

    public respuestaPeticiones crearPedido(CrearPedidoDTO pedidoDTO){
        if(repo.existsByUsuarioAndFechaPedido(pedidoDTO.getUsuario(), pedidoDTO.getFecha_pedido())){
            return respuestaPeticiones.repetido_pedido;
        }
        Pedido pedido = new Pedido();
        pedido.setEstado(pedidoDTO.getEstado());
        pedido.setFecha_pedido(pedidoDTO.getFecha_pedido());
        pedido.setUsuario(pedidoDTO.getUsuario());
        pedido.setCantidad_personas(pedidoDTO.getCantidad_personas());
        repo.save(pedido);
        return respuestaPeticiones.ok;
    }

    public respuestaPeticiones confirmarPedido(Integer id){
        Optional<Pedido> pedido = repo.findById(id);
        if(pedido.isEmpty()){
            return respuestaPeticiones.falta_pedido;
        }
        pedido.get().setEstado(Pedido.EstadosPedidos.Confirmado);
        repo.save(pedido.get());
        return respuestaPeticiones.ok;
    }

    public respuestaPeticiones cancelarPedido(Integer idPedido, Integer idMenuPlato){
                Optional<Pedido> pedido = repo.findById(idPedido);
        if(pedido.isEmpty()){
            return respuestaPeticiones.falta_pedido;
        }
        pedido.get().setEstado(Pedido.EstadosPedidos.Cancelado);
        serviceMenu.reponerStock(idMenuPlato, pedido.get().getCantidad_personas());
        repo.save(pedido.get());
        return respuestaPeticiones.ok;
    }

    public respuestaPeticiones modificarPedido(ModificarPedidoDTO pedidoDTO){
        Optional<Pedido> pedido= repo.findById(pedidoDTO.getId());
        if(pedido.isEmpty()){
            return respuestaPeticiones.falta_pedido;
        }
        Pedido p= pedido.get();
        if(p.getEstado()==Pedido.EstadosPedidos.Confirmado){
            return respuestaPeticiones.no_editable_pedido;
        }
        
        return respuestaPeticiones.ok;
    }

    public List<Pedido> pedidoPorUsuario(Integer id){
        List<Pedido> pedidos = repo.findByUsuario_IdUsuario(id);
        return pedidos;
    }

    public List<Pedido> pedidoPorUsuarioYEstado(Integer id, Pedido.EstadosPedidos estado){
        List<Pedido> pedidos = repo.findByUsuario_IdUsuarioAndEstado(id, estado);
        return pedidos;
    }

    public respuestaPeticiones confirmarSemana(Integer idUsuario, LocalDate fechaReferencia) {

    LocalDate inicioSemana = fechaReferencia.with(DayOfWeek.MONDAY);
    LocalDate finSemana    = inicioSemana.plusDays(4);

    List<Pedido> semana = repo.findByUsuario_IdUsuarioAndFechaPedidoBetween(
        idUsuario,
        inicioSemana,
        finSemana
    );

    if (semana.isEmpty()) {
        return respuestaPeticiones.falta_pedido;
    }

    for (Pedido p : semana) {
        p.setEstado(Pedido.EstadosPedidos.Confirmado);
    }

    repo.saveAll(semana);

    return respuestaPeticiones.ok;
}


    public enum respuestaPeticiones{
        ok,
        falta_pedido,
        repetido_pedido,
        no_editable_pedido,

    }
}
