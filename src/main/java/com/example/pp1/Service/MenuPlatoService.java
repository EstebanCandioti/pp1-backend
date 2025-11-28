package com.example.pp1.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.pp1.DTO.menuPlato.CrearMenuPlatoDTO;
import com.example.pp1.DTO.menuPlato.ModificarMenuPlatoDTO;
import com.example.pp1.Entity.MenuDia;
import com.example.pp1.Entity.MenuPlato;
import com.example.pp1.Entity.Plato;
import com.example.pp1.repository.MenuPlatoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MenuPlatoService {
    
    private final MenuPlatoRepository repo;
    private final MenuDiaService serviceMenuDia;
    private final PlatoService servicePlato;

    public MenuPlatoService( MenuPlatoRepository repo, MenuDiaService serviceMenuDia, PlatoService servicePlato){
        this.repo=repo;
        this.serviceMenuDia=serviceMenuDia;
        this.servicePlato= servicePlato;
    }


    public respuestaPeticiones agregarPlato(CrearMenuPlatoDTO menuPlatoDTO){
        Optional<MenuDia> menu = serviceMenuDia.obtenerMenuDia(menuPlatoDTO.getIdMenuDia());
        Optional<Plato> plato = servicePlato.traerPlato(menuPlatoDTO.getIdPlato());
        if(!menu.isPresent()){
            return respuestaPeticiones.falta_menuDia;
        }
        if(!plato.isPresent()){
            return respuestaPeticiones.falta_plato;
        }
        if (repo.existsByMenuDiaAndPlato(menu.get(), plato.get())){
            return respuestaPeticiones.duplicado_menu;
        }

        MenuPlato menuPlato = new MenuPlato();
        menuPlato.setMenuDia(menu.get());
        menuPlato.setPlato(plato.get());
        menuPlato.setStockDisponible(menuPlatoDTO.getStockInicial());
        repo.save(menuPlato);
        return  respuestaPeticiones.ok;
    }

    public respuestaPeticiones descontarStock(Integer id, int cantidad){
        Optional<MenuPlato> menu = repo.findById(id);
        if(!menu.isPresent()){
            return respuestaPeticiones.falta_menuPlato;
        }
        if(cantidad<=0){
            return respuestaPeticiones.stock_invalido;
        }
        menu.get().setStockDisponible(menu.get().getStockDisponible()-cantidad);
        repo.save(menu.get());
        return respuestaPeticiones.ok;
    }

    public respuestaPeticiones eliminarPlatoMenuDia (Integer id){
        Optional<MenuPlato> menu = repo.findById(id);
        if(!menu.isPresent()){
            return respuestaPeticiones.falta_menuPlato;
        }
        repo.deleteById(id);
        return respuestaPeticiones.ok;
    }

    public List<MenuPlato> obtenerPlatosPorMenuDia(Integer id){
        Optional<MenuDia> menu = serviceMenuDia.obtenerMenuDia(id);
        if(menu.isEmpty()){
            throw new EntityNotFoundException("No se encontro un menuDia con este id");
        }
        List<MenuPlato> platos = repo.findByMenuDia(menu.get());
        return platos;
    }

    public List<MenuPlato> obtenerPlatosPorFecha(LocalDate fecha){
        List<MenuPlato> platos = repo.findByMenuDia_Fecha(fecha);
        return platos;
    }

    public respuestaPeticiones hayStockDisponible(Integer idMenuPlato, int solicitado){
        Optional<MenuPlato> menu = repo.findById(idMenuPlato);
        if(menu.isEmpty()){
           return respuestaPeticiones.falta_menuPlato;
        }

        if(solicitado<=0){
           return respuestaPeticiones.stock_invalido;
        }

        return respuestaPeticiones.ok;
    }

    public respuestaPeticiones reponerStock(Integer idMenuPlato, int cantidad){
        Optional<MenuPlato> menu = repo.findById(idMenuPlato);
        if(!menu.isPresent()){
            return respuestaPeticiones.falta_menuPlato;
        }
        menu.get().setStockDisponible(menu.get().getStockDisponible()+cantidad);
        repo.save(menu.get());
        return respuestaPeticiones.ok;
    }

    public respuestaPeticiones modificarMenuPlato(Integer idMenuPlato, ModificarMenuPlatoDTO menuDTO){
        
        Optional<MenuPlato> menu= repo.findById(idMenuPlato);
        if(menu.isEmpty()){
            return respuestaPeticiones.falta_menuPlato;
        }

        Optional<Plato> plato = servicePlato.traerPlato(menuDTO.getIdPlato());
        if(plato.isEmpty()){
            return respuestaPeticiones.falta_plato;
        }

        menu.get().setPlato(plato.get());
        menu.get().setStockDisponible(menuDTO.getStock());
        repo.save(menu.get());
        return respuestaPeticiones.ok;
    }

    public List<MenuPlato> traerTodos(){
        return repo.findAll();
    }

    public enum respuestaPeticiones{
        plato_agregado,
        falta_plato,
        falta_menuDia,
        falta_menuPlato,
        duplicado_menu,
        ok,
        stock_invalido
    }
}
