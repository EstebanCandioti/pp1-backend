package com.example.pp1.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.pp1.DTO.pedido.ConfirmarPedidoDTO;
import com.example.pp1.DTO.pedido.CrearPedidoDTO;
import com.example.pp1.DTO.pedido.ModificarPedidoDTO;
import com.example.pp1.Entity.MenuDia;
import com.example.pp1.Entity.MenuPlato;
import com.example.pp1.Entity.Pedido;
import com.example.pp1.Entity.PedidoDia;
import com.example.pp1.Entity.Plato;
import com.example.pp1.Entity.Usuario;
import com.example.pp1.repository.MenuPlatoRepository;
import com.example.pp1.repository.PedidoDiaRepository;
import com.example.pp1.repository.PedidoRepository;
import com.example.pp1.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoService {

    private final PedidoRepository repo;
    private final MenuPlatoService serviceMenu;
    private final UsuarioRepository usuarioRepo;
    private final MenuPlatoRepository menuPlatoRepo;
    private final PedidoDiaRepository pedidoDiaRepo;
    private final NotificacionService notificacionService;

    public PedidoService(PedidoRepository repo,
            MenuPlatoService serviceMenu,
            UsuarioRepository usuarioRepo,
            MenuPlatoRepository menuPlatoRepo,
            PedidoDiaRepository pedidoDiaRepo,
            NotificacionService notificacionService) {
        this.repo = repo;
        this.serviceMenu = serviceMenu;
        this.usuarioRepo = usuarioRepo;
        this.menuPlatoRepo = menuPlatoRepo;
        this.pedidoDiaRepo = pedidoDiaRepo;
        this.notificacionService = notificacionService;
    }

    public respuestaPeticiones crearPedido(CrearPedidoDTO pedidoDTO) {
        Optional<Usuario> usuarioOptional = usuarioRepo.findById(pedidoDTO.getUsuario().getIdUsuario());
        if(usuarioOptional.isEmpty()){
            return respuestaPeticiones.falta_usuario;
        }
        
        if (repo.existsByUsuarioAndFechaPedidoAndEstadoNot(
                usuarioOptional.get(),
                pedidoDTO.getFecha_pedido(),
                Pedido.EstadosPedidos.Cancelado)) {
            return respuestaPeticiones.repetido_pedido;
        }

        Pedido pedido = new Pedido();
        pedido.setEstado(pedidoDTO.getEstado());
        pedido.setFechaPedido(pedidoDTO.getFecha_pedido());
        pedido.setUsuario(usuarioOptional.get());
        pedido.setCantidad_personas(pedidoDTO.getCantidad_personas());
        repo.save(pedido);
        return respuestaPeticiones.ok;
    }

    public respuestaPeticiones confirmarPedido(Integer id) {
        Optional<Pedido> pedido = repo.findById(id);
        if (pedido.isEmpty()) {
            return respuestaPeticiones.falta_pedido;
        }
        pedido.get().setEstado(Pedido.EstadosPedidos.Confirmado);
        repo.save(pedido.get());
        
        // Notificar al usuario
        try {
            notificacionService.notificarPedidoConfirmado(pedido.get());
        } catch (Exception e) {
            System.err.println("Error al crear notificación de pedido confirmado: " + e.getMessage());
        }
        
        return respuestaPeticiones.ok;
    }

    public respuestaPeticiones cancelarPedido(Integer idPedido, Integer idMenuPlato) {
        Optional<Pedido> pedido = repo.findById(idPedido);
        if (pedido.isEmpty()) {
            return respuestaPeticiones.falta_pedido;
        }
        pedido.get().setEstado(Pedido.EstadosPedidos.Cancelado);
        serviceMenu.reponerStock(idMenuPlato, pedido.get().getCantidad_personas());
        repo.save(pedido.get());
        
        // Notificar al usuario
        try {
            notificacionService.notificarPedidoCancelado(pedido.get());
        } catch (Exception e) {
            System.err.println("Error al crear notificación de pedido cancelado: " + e.getMessage());
        }
        
        return respuestaPeticiones.ok;
    }

    @Transactional
    public respuestaPeticiones modificarPedido(ModificarPedidoDTO pedidoDTO) {

        Optional<Pedido> pedidoOpt = repo.findById(pedidoDTO.getId());
        if (pedidoOpt.isEmpty()) {
            return respuestaPeticiones.falta_pedido;
        }

        Pedido pedido = pedidoOpt.get();

        if (pedido.getEstado() == Pedido.EstadosPedidos.Confirmado) {
            return respuestaPeticiones.no_editable_pedido;
        }

        pedido.setCantidad_personas(pedidoDTO.getCantidad_personas());
        repo.save(pedido);

        return respuestaPeticiones.ok;
    }

    public List<Pedido> pedidoPorUsuario(Integer id) {
        List<Pedido> pedidos = repo.findByUsuario_IdUsuario(id);
        return pedidos;
    }

    public List<Pedido> pedidoPorUsuarioYEstado(Integer id, Pedido.EstadosPedidos estado) {
        List<Pedido> pedidos = repo.findByUsuario_IdUsuarioAndEstado(id, estado);
        return pedidos;
    }

    public respuestaPeticiones confirmarSemana(Integer idUsuario, LocalDate fechaReferencia) {

        LocalDate inicioSemana = fechaReferencia.with(DayOfWeek.MONDAY).plusWeeks(1);
        LocalDate finSemana = inicioSemana.plusDays(4);

        List<Pedido> semana = repo.findByUsuario_IdUsuarioAndFechaPedidoBetween(
                idUsuario,
                inicioSemana,
                finSemana);

        if (semana.isEmpty()) {
            return respuestaPeticiones.falta_pedido;
        }

        for (Pedido p : semana) {
            if(p.getEstado()== Pedido.EstadosPedidos.Pendiente){
            p.setEstado(Pedido.EstadosPedidos.Confirmado);
            }
        }

        repo.saveAll(semana);

        return respuestaPeticiones.ok;
    }

    @Transactional
    public respuestaPeticiones confirmarPedido(ConfirmarPedidoDTO dto) {

        Usuario usuario = usuarioRepo.findById(dto.idUsuario())
                .orElseThrow();

        MenuPlato menuPlato = menuPlatoRepo.findById(dto.idMenuPlato())
                .orElseThrow();

        MenuDia menuDia = menuPlato.getMenuDia();
        Plato plato = menuPlato.getPlato();

        LocalDate fechaEntrega = (dto.fechaEntrega() != null)
                ? dto.fechaEntrega()
                : menuDia.getFecha();

        // 1) validar repetido (usuario + fechaEntrega)
        boolean yaExiste = repo.existsByUsuarioAndFechaPedidoAndEstadoNot(
                usuario,
                fechaEntrega,
                Pedido.EstadosPedidos.Cancelado);
        if (yaExiste)
            return respuestaPeticiones.repetido_pedido;

        // 2) validar stock (si descontás por cantidadPersonas)
        int cantidad = dto.cantidadPersonas();
        if (menuPlato.getStockDisponible() < cantidad) {
            return respuestaPeticiones.sin_stock;
        }

        // 3) crear Pedido (cabecera)
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(fechaEntrega);
        pedido.setCantidad_personas(cantidad);
        pedido.setEstado(Pedido.EstadosPedidos.Pendiente);
        repo.save(pedido);

        // 4) crear PedidoDia (detalle)
        PedidoDia pedidoDia = new PedidoDia();
        pedidoDia.setPedido(pedido);
        pedidoDia.setMenuDia(menuDia);
        pedidoDia.setPlato(plato);
        pedidoDia.setFechaEntrega(fechaEntrega);
        pedidoDiaRepo.save(pedidoDia);

        // 5) descontar stock
        menuPlato.setStockDisponible(menuPlato.getStockDisponible() - cantidad);
        menuPlatoRepo.save(menuPlato);

        return respuestaPeticiones.ok;
    }

    public respuestaPeticiones confirmarSemanaRestaurante(LocalDate fechaReferencia, int offset) {
        LocalDate lunesSemana = fechaReferencia.with(DayOfWeek.MONDAY).plusWeeks(offset);
        LocalDate viernesSemana = lunesSemana.plusDays(4);

        List<Pedido> pedidosSemana = repo.findByFechaPedidoBetween(lunesSemana, viernesSemana);
        if (pedidosSemana.isEmpty())
            return respuestaPeticiones.falta_pedido;

        for (Pedido pedido : pedidosSemana) {
            // Solo confirmar los que están Pendiente
            if (pedido.getEstado() == Pedido.EstadosPedidos.Pendiente) {
                pedido.setEstado(Pedido.EstadosPedidos.Confirmado);
            }
        }

        repo.saveAll(pedidosSemana);
        
        // Notificar a cada usuario con pedidos confirmados
        try {
            pedidosSemana.stream()
                .filter(p -> p.getEstado() == Pedido.EstadosPedidos.Confirmado)
                .collect(java.util.stream.Collectors.groupingBy(Pedido::getUsuario))
                .forEach((usuario, pedidos) -> {
                    try {
                        String asunto = "Pedidos de la semana confirmados";
                        String mensaje = "Tus pedidos de la semana del " + lunesSemana + " al " + viernesSemana + " han sido confirmados";
                        notificacionService.crearNotificacion(asunto, mensaje, usuario);
                    } catch (Exception e) {
                        System.err.println("Error al notificar usuario " + usuario.getIdUsuario() + ": " + e.getMessage());
                    }
                });
        } catch (Exception e) {
            System.err.println("Error general al crear notificaciones de semana confirmada: " + e.getMessage());
        }
        
        return respuestaPeticiones.ok;
    }

    public enum respuestaPeticiones {
        ok,
        falta_pedido,
        falta_usuario,
        repetido_pedido,
        no_editable_pedido,
        sin_stock
    }
}