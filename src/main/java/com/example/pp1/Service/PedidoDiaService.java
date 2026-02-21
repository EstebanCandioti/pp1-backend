package com.example.pp1.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.pp1.DTO.pedidoDia.CrearPedidoDiaDTO;
import com.example.pp1.DTO.pedidoDia.ModificarPedidoDiaDTO;
import com.example.pp1.DTO.pedidoDia.PedidoDiaDTO;
import com.example.pp1.DTO.pedidoSemanal.PedidoSemanaItemDTO;
import com.example.pp1.Entity.MenuDia;
import com.example.pp1.Entity.MenuPlato;
import com.example.pp1.Entity.Pedido;
import com.example.pp1.Entity.Pedido.EstadosPedidos;
import com.example.pp1.Entity.PedidoDia;
import com.example.pp1.Entity.Plato;
import com.example.pp1.repository.MenuDiaRepository;
import com.example.pp1.repository.MenuPlatoRepository;
import com.example.pp1.repository.PedidoDiaRepository;
import com.example.pp1.repository.PedidoRepository;
import com.example.pp1.repository.PlatoRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoDiaService {

    public enum RespuestaPeticiones {
        ok,
        falta_pedido,
        falta_menu_dia,
        falta_plato,
        falta_menu_plato,
        falta_pedido_dia,
        pedido_no_editable,
        stock_insuficiente
    }

    private final PedidoDiaRepository pedidoDiaRepo;
    private final PedidoRepository pedidoRepo;
    private final MenuDiaRepository menuDiaRepo;
    private final PlatoRepository platoRepo;
    private final MenuPlatoRepository menuPlatoRepo;
    private final MenuPlatoService menuPlatoService;
    private final NotificacionService notificacionService;

    public PedidoDiaService(
            PedidoDiaRepository pedidoDiaRepo,
            PedidoRepository pedidoRepo,
            MenuDiaRepository menuDiaRepo,
            PlatoRepository platoRepo,
            MenuPlatoRepository menuPlatoRepo,
            MenuPlatoService menuPlatoService,
            NotificacionService notificacionService) {
        this.pedidoDiaRepo = pedidoDiaRepo;
        this.pedidoRepo = pedidoRepo;
        this.menuDiaRepo = menuDiaRepo;
        this.platoRepo = platoRepo;
        this.menuPlatoRepo = menuPlatoRepo;
        this.menuPlatoService = menuPlatoService;
        this.notificacionService = notificacionService;
    }

    public RespuestaPeticiones crearPedidoDia(CrearPedidoDiaDTO dto) {

        Optional<Pedido> pedidoOpt = pedidoRepo.findById(dto.getIdPedido());
        if (pedidoOpt.isEmpty()) {
            return RespuestaPeticiones.falta_pedido;
        }
        Pedido pedido = pedidoOpt.get();

        // si el pedido está confirmado, no debería poder agregarse un día nuevo
        if (pedido.getEstado() == Pedido.EstadosPedidos.Confirmado) {
            return RespuestaPeticiones.pedido_no_editable;
        }

        Optional<MenuDia> menuDiaOpt = menuDiaRepo.findById(dto.getIdMenuDia());
        if (menuDiaOpt.isEmpty()) {
            return RespuestaPeticiones.falta_menu_dia;
        }
        MenuDia menuDia = menuDiaOpt.get();

        Optional<Plato> platoOpt = platoRepo.findById(dto.getIdPlato());
        if (platoOpt.isEmpty()) {
            return RespuestaPeticiones.falta_plato;
        }
        Plato plato = platoOpt.get();

        // Buscar MenuPlato para controlar stock
        Optional<MenuPlato> menuPlatoOpt = menuPlatoRepo.findByMenuDiaAndPlato(menuDia, plato);
        if (menuPlatoOpt.isEmpty()) {
            return RespuestaPeticiones.falta_menu_plato;
        }
        MenuPlato menuPlato = menuPlatoOpt.get();

        int cantidadPersonas = pedido.getCantidad_personas();

        // Validar stock usando el service de MenuPlato
        if (menuPlatoService.hayStockDisponible(menuPlato.getIdMenuPlato(),
                cantidadPersonas) == MenuPlatoService.respuestaPeticiones.stock_invalido) {
            return RespuestaPeticiones.stock_insuficiente;
        }

        // Descontar stock
        menuPlatoService.descontarStock(menuPlato.getIdMenuPlato(), cantidadPersonas);

        PedidoDia pedidoDia = new PedidoDia();
        pedidoDia.setPedido(pedido);
        pedidoDia.setMenuDia(menuDia);
        pedidoDia.setPlato(plato);

        LocalDate fechaEntrega = dto.getFechaEntrega() != null
                ? dto.getFechaEntrega()
                : menuDia.getFecha();

        pedidoDia.setFechaEntrega(fechaEntrega);

        pedidoDiaRepo.save(pedidoDia);

        return RespuestaPeticiones.ok;
    }

    public List<PedidoDiaDTO> listarPorPedido(Integer idPedido) {
        List<PedidoDia> lista = pedidoDiaRepo.findByPedido_IdPedido(idPedido);

        return lista.stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Transactional
    public RespuestaPeticiones eliminarPedidoDia(Integer idPedidoDia) {

        Optional<PedidoDia> pedidoDiaOpt = pedidoDiaRepo.findById(idPedidoDia);
        if (pedidoDiaOpt.isEmpty()) {
            return RespuestaPeticiones.falta_pedido_dia;
        }

        PedidoDia pedidoDia = pedidoDiaOpt.get();
        Pedido pedido = pedidoDia.getPedido();

        if(pedido == null){
            return RespuestaPeticiones.falta_pedido;
        }

        if (EstadosPedidos.Confirmado == pedido.getEstado()) {
            return RespuestaPeticiones.pedido_no_editable;
        }

        MenuDia menuDia = pedidoDia.getMenuDia();
        Plato plato = pedidoDia.getPlato();

        Optional<MenuPlato> menuPlatoOpt = menuPlatoRepo.findByMenuDiaAndPlato(menuDia, plato);

        if (menuPlatoOpt.isEmpty()) {
            return RespuestaPeticiones.falta_menu_plato;
        }

        MenuPlato menuPlato = menuPlatoOpt.get();
        int cantidadPersonas = pedido.getCantidad_personas();

        // Reponer stock
        menuPlatoService.reponerStock(menuPlato.getIdMenuPlato(), cantidadPersonas);

        // Eliminar detalle
        pedidoDiaRepo.delete(pedidoDia);

        // Si ya no quedan detalles, marcar pedido como cancelado y notificar
        boolean quedan = pedidoDiaRepo.existsByPedido_IdPedido(pedido.getIdPedido());
        if (!quedan) {
            pedido.setEstado(EstadosPedidos.Cancelado);
            pedidoRepo.save(pedido);
            
            // Notificar al usuario que su pedido fue cancelado
            try {
                notificacionService.notificarPedidoCancelado(pedido);
            } catch (Exception e) {
                System.err.println("Error al crear notificación de pedido cancelado: " + e.getMessage());
            }
        }

        return RespuestaPeticiones.ok;
    }

    @Transactional
    public RespuestaPeticiones modificarPedidoDia(ModificarPedidoDiaDTO dto) {

        Optional<PedidoDia> pedidoDiaOpt = pedidoDiaRepo.findById(dto.getIdPedidoDia());
        if (pedidoDiaOpt.isEmpty()) {
            return RespuestaPeticiones.falta_pedido_dia;
        }

        PedidoDia pedidoDia = pedidoDiaOpt.get();
        Pedido pedido = pedidoDia.getPedido();

        if (pedido.getEstado() == Pedido.EstadosPedidos.Confirmado) {
            return RespuestaPeticiones.pedido_no_editable;
        }

        // Cantidades
        int cantidadAnterior = pedido.getCantidad_personas();
        int cantidadNueva = dto.getCantidadPersonas();

        // Info anterior
        MenuDia menuDiaAnterior = pedidoDia.getMenuDia();
        Plato platoAnterior = pedidoDia.getPlato();

        Optional<MenuDia> menuDiaNuevoOpt = menuDiaRepo.findById(dto.getIdMenuDiaNuevo());
        if (menuDiaNuevoOpt.isEmpty()) {
            return RespuestaPeticiones.falta_menu_dia;
        }
        MenuDia menuDiaNuevo = menuDiaNuevoOpt.get();

        Optional<Plato> platoNuevoOpt = platoRepo.findById(dto.getIdPlatoNuevo());
        if (platoNuevoOpt.isEmpty()) {
            return RespuestaPeticiones.falta_plato;
        }
        Plato platoNuevo = platoNuevoOpt.get();

        // Recuperar MenuPlato anterior y nuevo
        Optional<MenuPlato> menuPlatoAnteriorOpt = menuPlatoRepo.findByMenuDiaAndPlato(menuDiaAnterior, platoAnterior);

        Optional<MenuPlato> menuPlatoNuevoOpt = menuPlatoRepo.findByMenuDiaAndPlato(menuDiaNuevo, platoNuevo);

        if (menuPlatoAnteriorOpt.isEmpty() || menuPlatoNuevoOpt.isEmpty()) {
            return RespuestaPeticiones.falta_menu_plato;
        }

        MenuPlato menuPlatoAnterior = menuPlatoAnteriorOpt.get();
        MenuPlato menuPlatoNuevo = menuPlatoNuevoOpt.get();

        // Reponer stock anterior
        menuPlatoService.reponerStock(
                menuPlatoAnterior.getIdMenuPlato(),
                cantidadAnterior);

        // Validar stock nuevo con cantidad NUEVA
        if (menuPlatoService.hayStockDisponible(
                menuPlatoNuevo.getIdMenuPlato(),
                cantidadNueva) == MenuPlatoService.respuestaPeticiones.stock_invalido) {

            // rollback manual del stock anterior
            menuPlatoService.descontarStock(
                    menuPlatoAnterior.getIdMenuPlato(),
                    cantidadAnterior);

            return RespuestaPeticiones.stock_insuficiente;
        }

        // Descontar stock nuevo
        menuPlatoService.descontarStock(
                menuPlatoNuevo.getIdMenuPlato(),
                cantidadNueva);

        // Actualizar Pedido (cabecera)
        pedido.setCantidad_personas(cantidadNueva);

        // Actualizar PedidoDia
        pedidoDia.setMenuDia(menuDiaNuevo);
        pedidoDia.setPlato(platoNuevo);
        pedidoDia.setFechaEntrega(menuDiaNuevo.getFecha());

        pedidoDiaRepo.save(pedidoDia);
        pedidoRepo.save(pedido);

        return RespuestaPeticiones.ok;
    }

    public List<PedidoSemanaItemDTO> listarPedidosSemana(LocalDate fechaReferencia, int offset) {
        LocalDate lunesSemana = fechaReferencia.with(DayOfWeek.MONDAY).plusWeeks(offset);
        LocalDate viernesSemana = lunesSemana.plusDays(4);

        return pedidoDiaRepo.listarPedidosSemana(lunesSemana, viernesSemana);
    }

    private PedidoDiaDTO convertirADTO(PedidoDia pedidoDia) {
        PedidoDiaDTO dto = new PedidoDiaDTO();
        dto.setIdPedidoDia(pedidoDia.getId_pedido_dia());
        dto.setFechaEntrega(pedidoDia.getFechaEntrega());
        dto.setIdPedido(pedidoDia.getPedido().getIdPedido());
        dto.setIdMenuDia(pedidoDia.getMenuDia().getIdMenuDia());
        dto.setIdPlato(pedidoDia.getPlato().getId_plato());
        dto.setNombrePlato(pedidoDia.getPlato().getNombre());
        return dto;
    }
}