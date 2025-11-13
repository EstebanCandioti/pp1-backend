package com.example.pp1.Service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.pp1.DTO.ActualizarMenuDiaDTO;
import com.example.pp1.DTO.CrearMenuDiaDTO;
import com.example.pp1.Entity.MenuDia;
import com.example.pp1.repository.MenuDiaRepository;

@Service
public class MenuDiaService {
    
    private MenuDiaRepository repo;
    private UsuarioService serviceUsuario;

    public MenuDiaService(MenuDiaRepository repo, UsuarioService serviceUsuario){
        this.repo=repo;
        this.serviceUsuario=serviceUsuario;
    }

    public respuestaPeticiones crearMenuDia(CrearMenuDiaDTO menu){
        if(serviceUsuario.obtenerUsuario(menu.getId_usuario()).isEmpty()){
            return respuestaPeticiones.falta_usuario;
        }
        MenuDia nuevoMenu = new MenuDia();
        nuevoMenu.setUsuario(serviceUsuario.obtenerUsuario(menu.getId_usuario()).get());
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
        if(menu.isEmpty()){
            return respuestaPeticiones.falta_menu;
        }
        Boolean estado= menu.get().getPublicado();
        menu.get().setPublicado(!estado);
        repo.save(menu.get());
        return respuestaPeticiones.menu_actualizado;
    }

    public enum respuestaPeticiones{
        ok,
        menu_creado,
        falta_usuario,
        falta_menu,
        menu_actualizado
    }
}
