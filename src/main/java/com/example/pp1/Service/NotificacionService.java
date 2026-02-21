package com.example.pp1.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.pp1.DTO.notificacion.CrearNotificacionDTO;
import com.example.pp1.DTO.notificacion.NotificacionDTO;
import com.example.pp1.Entity.MenuDia;
import com.example.pp1.Entity.Notificacion;
import com.example.pp1.Entity.Pedido;
import com.example.pp1.Entity.Plato;
import com.example.pp1.Entity.Usuario;
import com.example.pp1.repository.NotificacionRepository;
import com.example.pp1.repository.UsuarioRepository;

@Service
public class NotificacionService {

    public enum RespuestaPeticiones {
        ok,
        falta_usuario,
        falta_notificacion
    }

    private final NotificacionRepository notiRepo;
    private final UsuarioRepository usuarioRepo;

    public NotificacionService(NotificacionRepository notiRepo, UsuarioRepository usuarioRepo) {
        this.notiRepo = notiRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // 1 Crear notificación genérica desde DTO (para el controller)
    public RespuestaPeticiones crearNotificacion(CrearNotificacionDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findById(dto.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            return RespuestaPeticiones.falta_usuario;
        }
        Usuario usuario = usuarioOpt.get();

        crearNotificacion(dto.getAsunto(), dto.getMensaje(), usuario);
        return RespuestaPeticiones.ok;
    }

    // 2 Crear notificación genérica para usar desde otros services
    public Notificacion crearNotificacion(String asunto, String mensaje, Usuario usuario) {
        Notificacion n = new Notificacion();
        n.setUsuario(usuario);
        n.setAsunto(asunto);
        n.setMensaje(mensaje);
        n.setFechaEnvio(new Date());
        n.setLeida(false); // Por defecto no leída
        return notiRepo.save(n);
    }

    // 3 Listar notificaciones de un usuario
    public List<NotificacionDTO> obtenerNotificacionesUsuario(Integer idUsuario) {
        List<Notificacion> lista = notiRepo.findByUsuario_IdUsuarioOrderByFechaEnvioDesc(idUsuario);
        return lista.stream()
                .map(this::convertirADTO)
                .toList();
    }

    // 4 Eliminar todas las notificaciones de un usuario
    public RespuestaPeticiones eliminarNotificacionesUsuario(Integer idUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            return RespuestaPeticiones.falta_usuario;
        }
        notiRepo.deleteByUsuario_IdUsuario(idUsuario);
        return RespuestaPeticiones.ok;
    }

    // 5 Marcar una notificación como leída
    public RespuestaPeticiones marcarComoLeida(Integer idNotificacion) {
        Optional<Notificacion> notificacionOpt = notiRepo.findById(idNotificacion);
        if (notificacionOpt.isEmpty()) {
            return RespuestaPeticiones.falta_notificacion;
        }

        Notificacion notificacion = notificacionOpt.get();
        notificacion.setLeida(true);
        notiRepo.save(notificacion);

        return RespuestaPeticiones.ok;
    }

    // 6 Marcar todas las notificaciones de un usuario como leídas
    public RespuestaPeticiones marcarTodasLeidasUsuario(Integer idUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            return RespuestaPeticiones.falta_usuario;
        }

        List<Notificacion> notificaciones = notiRepo.findByUsuario_IdUsuarioOrderByFechaEnvioDesc(idUsuario);

        for (Notificacion n : notificaciones) {
            if (!n.getLeida()) {
                n.setLeida(true);
            }
        }

        notiRepo.saveAll(notificaciones);
        return RespuestaPeticiones.ok;
    }

    // Métodos de negocio (para llamar desde PedidoService, MenuDiaService, etc.)

    public void notificarPedidoCreado(Pedido pedido) {
        Usuario usuario = pedido.getUsuario();
        String asunto = "Pedido creado";
        String mensaje = "Tu pedido del " + pedido.getFechaPedido() + " fue registrado correctamente.";
        crearNotificacion(asunto, mensaje, usuario);
    }

    public void notificarPedidoConfirmado(Pedido pedido) {
        Usuario usuario = pedido.getUsuario();
        String asunto = "Pedido confirmado";
        String mensaje = "Tu pedido del " + pedido.getFechaPedido() + " ha sido confirmado.";
        crearNotificacion(asunto, mensaje, usuario);
    }

    public void notificarPedidoModificado(Pedido pedido) {
        Usuario usuario = pedido.getUsuario();
        String asunto = "Pedido modificado";
        String mensaje = "Tu pedido del " + pedido.getFechaPedido() + " fue modificado.";
        crearNotificacion(asunto, mensaje, usuario);
    }

    public void notificarPedidoCancelado(Pedido pedido) {
        Usuario usuario = pedido.getUsuario();
        String asunto = "Pedido cancelado";
        String mensaje = "Tu pedido del " + pedido.getFechaPedido() + " fue cancelado.";
        crearNotificacion(asunto, mensaje, usuario);
    }

    public void notificarMenuPublicado(MenuDia menuDia, Usuario usuario) {
        String asunto = "Menú publicado";
        String mensaje = "Ya está disponible el menú del día " + menuDia.getFecha() + ".";
        crearNotificacion(asunto, mensaje, usuario);
    }

    public void notificarStockInsuficiente(Pedido pedido, Plato plato) {
        Usuario usuario = pedido.getUsuario();
        String asunto = "Stock insuficiente";
        String mensaje = "Tu pedido del " + pedido.getFechaPedido()
                + " no pudo procesarse por falta de stock del plato " + plato.getNombre() + ".";
        crearNotificacion(asunto, mensaje, usuario);
    }

    public void notificarNuevoPedidoParaRestaurante(Pedido pedido, Usuario usuarioRestaurante) {
        Usuario usuarioCliente = pedido.getUsuario();
        String asunto = "Nuevo pedido recibido";
        String mensaje = "El usuario " + usuarioCliente.getNombre() + " realizó un pedido para el día "
                + pedido.getFechaPedido() + ".";
        crearNotificacion(asunto, mensaje, usuarioRestaurante);
    }

    // Mapper entidad → DTO
    private NotificacionDTO convertirADTO(Notificacion n) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setIdNotificacion(n.getIdNotificacion());
        dto.setFechaEnvio(n.getFechaEnvio());
        dto.setAsunto(n.getAsunto());
        dto.setMensaje(n.getMensaje());
        dto.setLeida(n.getLeida() != null ? n.getLeida() : false);
        if (n.getUsuario() != null) {
            dto.setIdUsuario(n.getUsuario().getIdUsuario());
            dto.setNombreUsuario(n.getUsuario().getNombre());
        }
        return dto;
    }
}