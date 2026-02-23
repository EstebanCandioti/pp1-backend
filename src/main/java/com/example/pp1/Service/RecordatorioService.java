package com.example.pp1.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.pp1.Entity.Configuracion;
import com.example.pp1.Entity.Pedido;
import com.example.pp1.Entity.Usuario;
import com.example.pp1.Entity.UsuarioAsistencia;
import com.example.pp1.repository.PedidoRepository;
import com.example.pp1.repository.UsuarioRepository;

@Service
public class RecordatorioService {

    private final UsuarioRepository usuarioRepo;
    private final PedidoRepository pedidoRepo;
    private final NotificacionService notificacionService;
    private final ConfiguracionService configuracionService;
    private final com.example.pp1.repository.MenuDiaRepository menuDiaRepo;

    public RecordatorioService(
            UsuarioRepository usuarioRepo,
            PedidoRepository pedidoRepo,
            NotificacionService notificacionService,
            ConfiguracionService configuracionService,
            com.example.pp1.repository.MenuDiaRepository menuDiaRepo) {
        this.usuarioRepo = usuarioRepo;
        this.pedidoRepo = pedidoRepo;
        this.notificacionService = notificacionService;
        this.configuracionService = configuracionService;
        this.menuDiaRepo = menuDiaRepo;
    }

    /**
     * Envía recordatorios los jueves a las 9:00 AM
     * Notifica a usuarios que tienen días de asistencia la semana siguiente
     * pero no han completado todos sus pedidos
     */
    public void enviarRecordatoriosJueves() {
        System.out.println("=== INICIANDO RECORDATORIOS JUEVES 9:00 AM ===");
        
        List<Usuario> usuariosActivos = obtenerUsuariosActivosNoRestaurante();
        LocalDate lunesSiguiente = obtenerLunesSiguiente();
        LocalDate viernesSiguiente = lunesSiguiente.plusDays(4);
        
        int notificacionesEnviadas = 0;
        
        for (Usuario usuario : usuariosActivos) {
            // Obtener días de asistencia del usuario para la semana siguiente
            Set<LocalDate> diasAsistencia = obtenerDiasAsistenciaSemana(usuario, lunesSiguiente, viernesSiguiente);
            
            if (diasAsistencia.isEmpty()) {
                continue; // Usuario no tiene asistencia la semana siguiente
            }
            
            // Obtener pedidos ya realizados para esa semana
            List<Pedido> pedidosUsuario = pedidoRepo.findByUsuario_IdUsuarioAndFechaPedidoBetween(
                    usuario.getIdUsuario(),
                    lunesSiguiente,
                    viernesSiguiente
            );
            
            // Filtrar solo pedidos no cancelados
            Set<LocalDate> diasConPedido = pedidosUsuario.stream()
                    .filter(p -> p.getEstado() != Pedido.EstadosPedidos.Cancelado)
                    .map(Pedido::getFechaPedido)
                    .collect(Collectors.toSet());
            
            // Calcular días faltantes
            Set<LocalDate> diasFaltantes = new HashSet<>(diasAsistencia);
            diasFaltantes.removeAll(diasConPedido);
            
            if (!diasFaltantes.isEmpty()) {
                int cantidadFaltante = diasFaltantes.size();
                String asunto = "Recordatorio: Pedidos semanales";
                String mensaje = String.format(
                    "Recordá realizar tus pedidos para la semana que viene. Te faltan %d día%s por pedir.",
                    cantidadFaltante,
                    cantidadFaltante == 1 ? "" : "s"
                );
                
                try {
                    notificacionService.crearNotificacion(asunto, mensaje, usuario);
                    notificacionesEnviadas++;
                    System.out.println("Recordatorio enviado a: " + usuario.getNombre() + " " + usuario.getApellido() + 
                                     " - Días faltantes: " + cantidadFaltante);
                } catch (Exception e) {
                    System.err.println("Error al enviar recordatorio a usuario " + usuario.getIdUsuario() + ": " + e.getMessage());
                }
            }
        }
        
        System.out.println("=== FIN RECORDATORIOS JUEVES - Enviados: " + notificacionesEnviadas + " ===");
    }

    /**
     * Envía recordatorios de urgencia los viernes a las 9:00 AM
     * Incluye el horario límite en el mensaje
     */
    public void enviarRecordatoriosViernes() {
        System.out.println("=== INICIANDO RECORDATORIOS URGENTES VIERNES 9:00 AM ===");
        
        List<Usuario> usuariosActivos = obtenerUsuariosActivosNoRestaurante();
        LocalDate lunesSiguiente = obtenerLunesSiguiente();
        LocalDate viernesSiguiente = lunesSiguiente.plusDays(4);
        
        // Obtener horario límite desde configuración
        Configuracion config = configuracionService.obtenerConfiguracion();
        String horarioLimite = config.getHorarioLimite();
        
        int notificacionesEnviadas = 0;
        
        for (Usuario usuario : usuariosActivos) {
            // Obtener días de asistencia del usuario para la semana siguiente
            Set<LocalDate> diasAsistencia = obtenerDiasAsistenciaSemana(usuario, lunesSiguiente, viernesSiguiente);
            
            if (diasAsistencia.isEmpty()) {
                continue;
            }
            
            // Obtener pedidos ya realizados para esa semana
            List<Pedido> pedidosUsuario = pedidoRepo.findByUsuario_IdUsuarioAndFechaPedidoBetween(
                    usuario.getIdUsuario(),
                    lunesSiguiente,
                    viernesSiguiente
            );
            
            // Filtrar solo pedidos no cancelados
            Set<LocalDate> diasConPedido = pedidosUsuario.stream()
                    .filter(p -> p.getEstado() != Pedido.EstadosPedidos.Cancelado)
                    .map(Pedido::getFechaPedido)
                    .collect(Collectors.toSet());
            
            // Calcular días faltantes
            Set<LocalDate> diasFaltantes = new HashSet<>(diasAsistencia);
            diasFaltantes.removeAll(diasConPedido);
            
            if (!diasFaltantes.isEmpty()) {
                int cantidadFaltante = diasFaltantes.size();
                String asunto = "¡Último día! Recordatorio de pedidos";
                String mensaje = String.format(
                    "¡Último día! Recordá que el horario límite para pedir es las %s. Te faltan %d día%s por pedir.",
                    horarioLimite,
                    cantidadFaltante,
                    cantidadFaltante == 1 ? "" : "s"
                );
                
                try {
                    notificacionService.crearNotificacion(asunto, mensaje, usuario);
                    notificacionesEnviadas++;
                    System.out.println("Recordatorio URGENTE enviado a: " + usuario.getNombre() + " " + usuario.getApellido() + 
                                     " - Días faltantes: " + cantidadFaltante);
                } catch (Exception e) {
                    System.err.println("Error al enviar recordatorio urgente a usuario " + usuario.getIdUsuario() + ": " + e.getMessage());
                }
            }
        }
        
        System.out.println("=== FIN RECORDATORIOS VIERNES - Enviados: " + notificacionesEnviadas + " ===");
    }

    /**
     * Obtiene todos los usuarios activos que NO son usuarios restaurante
     */
    private List<Usuario> obtenerUsuariosActivosNoRestaurante() {
        return usuarioRepo.findAll().stream()
                .filter(u -> u.getActivo() != null && u.getActivo())
                .filter(u -> u.getEs_usuario_restaurante() == null || !u.getEs_usuario_restaurante())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el lunes de la semana siguiente
     */
    private LocalDate obtenerLunesSiguiente() {
        LocalDate hoy = LocalDate.now();
        LocalDate lunesEstaSemana = hoy.with(DayOfWeek.MONDAY);
        return lunesEstaSemana.plusWeeks(1);
    }

    /**
     * Obtiene los días de asistencia del usuario dentro del rango de fechas especificado
     * Solo devuelve días entre lunes y viernes
     */
    private Set<LocalDate> obtenerDiasAsistenciaSemana(Usuario usuario, LocalDate inicio, LocalDate fin) {
        Set<LocalDate> diasAsistencia = new HashSet<>();
        
        if (usuario.getDiasAsistencia() == null || usuario.getDiasAsistencia().isEmpty()) {
            return diasAsistencia;
        }
        
        // Mapear días de asistencia a DayOfWeek
        Set<DayOfWeek> diasSemanaAsistencia = usuario.getDiasAsistencia().stream()
                .map(UsuarioAsistencia::getDia)
                .map(this::convertirStringADayOfWeek)
                .filter(day -> day != null)
                .collect(Collectors.toSet());
        
        // Recorrer cada día entre inicio y fin
        LocalDate fecha = inicio;
        while (!fecha.isAfter(fin)) {
            DayOfWeek diaSemana = fecha.getDayOfWeek();
            
            // Solo considerar lunes a viernes
            if (diaSemana.getValue() <= 5 && diasSemanaAsistencia.contains(diaSemana)) {
                diasAsistencia.add(fecha);
            }
            
            fecha = fecha.plusDays(1);
        }
        
        return diasAsistencia;
    }

    /**
     * Convierte un string (LUNES, MARTES, etc.) a DayOfWeek
     */
    private DayOfWeek convertirStringADayOfWeek(String dia) {
        if (dia == null) return null;
        
        return switch(dia.toUpperCase()) {
            case "LUNES" -> DayOfWeek.MONDAY;
            case "MARTES" -> DayOfWeek.TUESDAY;
            case "MIERCOLES", "MIÉRCOLES" -> DayOfWeek.WEDNESDAY;
            case "JUEVES" -> DayOfWeek.THURSDAY;
            case "VIERNES" -> DayOfWeek.FRIDAY;
            case "SABADO", "SÁBADO" -> DayOfWeek.SATURDAY;
            case "DOMINGO" -> DayOfWeek.SUNDAY;
            default -> null;
        };
    }

    // ========================================================================
    // RECORDATORIOS DE MENÚS FALTANTES PARA ADMINISTRADOR
    // ========================================================================

    /**
     * Envía recordatorios de menús faltantes los jueves a las 9:00 AM
     * Notifica al administrador del restaurante si faltan menús por configurar/publicar
     */
    public void enviarRecordatoriosMenusJueves() {
        System.out.println("=== INICIANDO RECORDATORIOS MENÚS JUEVES 9:00 AM ===");
        
        LocalDate lunesSiguiente = obtenerLunesSiguiente();
        LocalDate viernesSiguiente = lunesSiguiente.plusDays(4);
        
        List<String> diasFaltantes = obtenerDiasSinMenuPublicado(lunesSiguiente, viernesSiguiente);
        
        if (diasFaltantes.isEmpty()) {
            System.out.println("Todos los menús de la semana siguiente están publicados ✓");
            System.out.println("=== FIN RECORDATORIOS MENÚS JUEVES - No se enviaron notificaciones ===");
            return;
        }
        
        // Obtener usuario administrador restaurante
        List<Usuario> admins = obtenerUsuariosAdminRestaurante();
        
        if (admins.isEmpty()) {
            System.err.println("ERROR: No se encontró ningún usuario administrador del restaurante");
            System.out.println("=== FIN RECORDATORIOS MENÚS JUEVES - Error ===");
            return;
        }
        
        String diasFaltantesStr = String.join(", ", diasFaltantes);
        String asunto = "Recordatorio: Menús semanales";
        String mensaje = "Recordá configurar los menús de la semana que viene. Faltan menús para: " + diasFaltantesStr + ".";
        
        int notificacionesEnviadas = 0;
        for (Usuario admin : admins) {
            try {
                notificacionService.crearNotificacion(asunto, mensaje, admin);
                notificacionesEnviadas++;
                System.out.println("Recordatorio de menús enviado a: " + admin.getNombre() + " " + admin.getApellido() + 
                                 " - Días faltantes: " + diasFaltantes.size());
            } catch (Exception e) {
                System.err.println("Error al enviar recordatorio de menús a admin " + admin.getIdUsuario() + ": " + e.getMessage());
            }
        }
        
        System.out.println("=== FIN RECORDATORIOS MENÚS JUEVES - Enviados: " + notificacionesEnviadas + " ===");
    }

    /**
     * Envía recordatorios de urgencia de menús faltantes los viernes a las 9:00 AM
     * Mensaje más urgente para el administrador
     */
    public void enviarRecordatoriosMenusViernes() {
        System.out.println("=== INICIANDO RECORDATORIOS MENÚS URGENTES VIERNES 9:00 AM ===");
        
        LocalDate lunesSiguiente = obtenerLunesSiguiente();
        LocalDate viernesSiguiente = lunesSiguiente.plusDays(4);
        
        List<String> diasFaltantes = obtenerDiasSinMenuPublicado(lunesSiguiente, viernesSiguiente);
        
        if (diasFaltantes.isEmpty()) {
            System.out.println("Todos los menús de la semana siguiente están publicados ✓");
            System.out.println("=== FIN RECORDATORIOS MENÚS VIERNES - No se enviaron notificaciones ===");
            return;
        }
        
        // Obtener usuario administrador restaurante
        List<Usuario> admins = obtenerUsuariosAdminRestaurante();
        
        if (admins.isEmpty()) {
            System.err.println("ERROR: No se encontró ningún usuario administrador del restaurante");
            System.out.println("=== FIN RECORDATORIOS MENÚS VIERNES - Error ===");
            return;
        }
        
        String diasFaltantesStr = String.join(", ", diasFaltantes);
        String asunto = "¡Último día! Recordatorio de menús";
        String mensaje = "¡Último día! Recordá configurar los menús faltantes para la semana que viene: " + diasFaltantesStr + ".";
        
        int notificacionesEnviadas = 0;
        for (Usuario admin : admins) {
            try {
                notificacionService.crearNotificacion(asunto, mensaje, admin);
                notificacionesEnviadas++;
                System.out.println("Recordatorio URGENTE de menús enviado a: " + admin.getNombre() + " " + admin.getApellido() + 
                                 " - Días faltantes: " + diasFaltantes.size());
            } catch (Exception e) {
                System.err.println("Error al enviar recordatorio urgente de menús a admin " + admin.getIdUsuario() + ": " + e.getMessage());
            }
        }
        
        System.out.println("=== FIN RECORDATORIOS MENÚS VIERNES - Enviados: " + notificacionesEnviadas + " ===");
    }

    /**
     * Obtiene los días laborales (L-V) que NO tienen menú publicado
     * @return Lista de strings con formato "Lunes 24/02", "Miércoles 26/02", etc.
     */
    private List<String> obtenerDiasSinMenuPublicado(LocalDate inicio, LocalDate fin) {
        List<String> diasFaltantes = new java.util.ArrayList<>();
        
        LocalDate fecha = inicio;
        while (!fecha.isAfter(fin)) {
            // Solo considerar días laborales (Lunes a Viernes)
            if (fecha.getDayOfWeek().getValue() <= 5) {
                // Buscar menús para esta fecha
                List<com.example.pp1.Entity.MenuDia> menus = menuDiaRepo.findByFecha(fecha);
                
                // Verificar si existe al menos un menú publicado para este día
                boolean tieneMenuPublicado = menus.stream()
                        .anyMatch(menu -> menu.getPublicado() != null && menu.getPublicado());
                
                if (!tieneMenuPublicado) {
                    // Agregar el día a la lista de faltantes con formato legible
                    String diaFormateado = formatearDiaEspanol(fecha);
                    diasFaltantes.add(diaFormateado);
                }
            }
            
            fecha = fecha.plusDays(1);
        }
        
        return diasFaltantes;
    }

    /**
     * Formatea una fecha a formato legible en español
     * Ejemplo: "Lunes 24/02", "Martes 25/02"
     */
    private String formatearDiaEspanol(LocalDate fecha) {
        String nombreDia = switch(fecha.getDayOfWeek()) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
        
        int dia = fecha.getDayOfMonth();
        int mes = fecha.getMonthValue();
        
        return String.format("%s %02d/%02d", nombreDia, dia, mes);
    }

    /**
     * Obtiene todos los usuarios administradores del restaurante
     */
    private List<Usuario> obtenerUsuariosAdminRestaurante() {
        return usuarioRepo.findAll().stream()
                .filter(u -> u.getActivo() != null && u.getActivo())
                .filter(u -> u.getEs_usuario_restaurante() != null && u.getEs_usuario_restaurante())
                .collect(Collectors.toList());
    }
}