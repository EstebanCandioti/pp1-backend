package com.example.pp1.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.pp1.DTO.menuDIa.ActualizarMenuDiaDTO;
import com.example.pp1.DTO.menuDIa.CrearMenuDiaDTO;
import com.example.pp1.Entity.MenuDia;
import com.example.pp1.repository.MenuDiaRepository;

@Service
public class MenuDiaService {
    
    private final MenuDiaRepository repo;
    private final UsuarioService serviceUsuario;
    private final NotificacionService notificacionService;

    public MenuDiaService(MenuDiaRepository repo, UsuarioService serviceUsuario, NotificacionService notificacionService){
        this.repo=repo;
        this.serviceUsuario=serviceUsuario;
        this.notificacionService=notificacionService;
    }

    public respuestaPeticiones crearMenuDia(CrearMenuDiaDTO menu){
        if(serviceUsuario.obtenerUsuario(menu.getId_usuario()).isEmpty()){
            return respuestaPeticiones.falta_usuario;
        }
        MenuDia nuevoMenu = new MenuDia();
        nuevoMenu.setUsuarioCreador(serviceUsuario.obtenerUsuario(menu.getId_usuario()).get());
        nuevoMenu.setFecha(menu.getFecha());
        nuevoMenu.setDescripcion(menu.getDescripcion());
        nuevoMenu.setPublicado(menu.getPublicado());
        nuevoMenu.setStock_total(menu.getStock_total());
        repo.save(nuevoMenu);
        return respuestaPeticiones.menu_creado;
    }


    public respuestaPeticiones actualizarMenuDia(ActualizarMenuDiaDTO menuDTO){
        Optional<MenuDia> menu = repo.findById(menuDTO.getId());
        if(menu.isEmpty()){
            return respuestaPeticiones.falta_menu;
        }
        menu.get().setDescripcion(menuDTO.getDescripcion());
        menu.get().setFecha(menuDTO.getFecha());
        menu.get().setStock_total(menuDTO.getStock_total());
        repo.save(menu.get());
        return respuestaPeticiones.menu_actualizado;
    }

    public respuestaPeticiones cambiarEstadoMenuDia(Integer id){
        Optional<MenuDia> menu = repo.findById(id);
        if(!menu.isPresent()){
            return respuestaPeticiones.falta_menu;
        }
        Boolean estadoAnterior = menu.get().getPublicado();
        Boolean estadoNuevo = !estadoAnterior;
        menu.get().setPublicado(estadoNuevo);
        repo.save(menu.get());
        
        // Si se está publicando (cambió de false a true), notificar usuarios
        if (estadoNuevo && !estadoAnterior) {
            try {
                MenuDia menuPublicado = menu.get();
                LocalDate fechaMenu = menuPublicado.getFecha();
                
                // Obtener día de la semana del menú
                DayOfWeek diaSemanaJava = fechaMenu.getDayOfWeek();
                String diaSemanaStr = convertirDayOfWeekAString(diaSemanaJava);
                
                // Obtener todos los usuarios activos
                List<com.example.pp1.Entity.Usuario> todosUsuarios = serviceUsuario.obtenerUsuarios();
                
                // Filtrar usuarios activos que tengan ese día en su asistencia
                todosUsuarios.stream()
                    .filter(u -> u.getActivo() != null && u.getActivo())
                    .filter(u -> tieneAsistenciaEnDia(u, diaSemanaStr))
                    .forEach(usuario -> {
                        try {
                            notificacionService.notificarMenuPublicado(menuPublicado, usuario);
                        } catch (Exception e) {
                            System.err.println("Error al notificar usuario " + usuario.getIdUsuario() + " sobre menú publicado: " + e.getMessage());
                        }
                    });
                    
            } catch (Exception e) {
                System.err.println("Error general al crear notificaciones de menú publicado: " + e.getMessage());
            }
        }
        
        return respuestaPeticiones.menu_actualizado;
    }
    
    private String convertirDayOfWeekAString(DayOfWeek day) {
        return switch(day) {
            case MONDAY -> "LUNES";
            case TUESDAY -> "MARTES";
            case WEDNESDAY -> "MIERCOLES";
            case THURSDAY -> "JUEVES";
            case FRIDAY -> "VIERNES";
            case SATURDAY -> "SABADO";
            case SUNDAY -> "DOMINGO";
        };
    }
    
    private boolean tieneAsistenciaEnDia(com.example.pp1.Entity.Usuario usuario, String dia) {
        if (usuario.getDiasAsistencia() == null || usuario.getDiasAsistencia().isEmpty()) {
            return false;
        }
        return usuario.getDiasAsistencia().stream()
            .anyMatch(asist -> dia.equalsIgnoreCase(asist.getDia()));
    }

    public List<MenuDia> obtenerMenusDia(){
        List<MenuDia> menus = repo.findAll();
        return menus;
    }

    public Optional<MenuDia> obtenerMenuDia(Integer id){
        Optional<MenuDia> menu= repo.findById(id);
        return menu;

    }

    public List<MenuDia> obtenerMenusDiaFecha(LocalDate fecha){
        List<MenuDia> menus = repo.findByFecha(fecha);
        return menus;
    }

    public List<MenuDia> obtenerMenusSemana(LocalDate fechaReferencia, int offset) {
        LocalDate inicioSemana = fechaReferencia.with(DayOfWeek.MONDAY).plusWeeks(offset);
        LocalDate finSemana = inicioSemana.plusDays(4);
        System.out.println("fechaReferencia=" + fechaReferencia);
        System.out.println("offset=" + offset);
        System.out.println("inicioSemana=" + inicioSemana);
        System.out.println("finSemana=" + finSemana);
        return repo.findByFechaBetween(inicioSemana, finSemana);
    }

    public enum respuestaPeticiones{
        ok,
        menu_creado,
        falta_usuario,
        falta_menu,
        menu_actualizado
    }
}