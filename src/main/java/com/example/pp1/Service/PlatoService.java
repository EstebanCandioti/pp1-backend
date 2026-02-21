package com.example.pp1.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.pp1.DTO.plato.ActualizarPlatoDTO;
import com.example.pp1.DTO.plato.RegistrarPlatoDTO;
import com.example.pp1.Entity.Plato;
import com.example.pp1.repository.PlatoRepository;

@Service
public class PlatoService {

    private final  PlatoRepository repo;

    public PlatoService (PlatoRepository repo){
        this.repo=repo;
    }

    public respuestasSolicitudes crearPlato(RegistrarPlatoDTO platoDTO){
        Plato plato = new Plato();
        plato.setCategoria(platoDTO.getCategoria());
        plato.setDescripcion(platoDTO.getDescripcion());
        plato.setImagen(platoDTO.getImagen());
        plato.setNombre(platoDTO.getNombre());
        repo.save(plato);
        return respuestasSolicitudes.plato_creado;
    }

    public respuestasSolicitudes modificarPlato (ActualizarPlatoDTO platoDTO){
        Optional<Plato> plato = repo.findById(platoDTO.getId_plato());
        if(!plato.isPresent()){
            return respuestasSolicitudes.falta_plato;
        }
        plato.get().setCategoria(platoDTO.getCategoria());
        plato.get().setDescripcion(platoDTO.getDescripcion());
        plato.get().setImagen(platoDTO.getImagen());
        plato.get().setNombre(platoDTO.getNombre());
        repo.save(plato.get());
        return respuestasSolicitudes.plato_actualizado;
    }

    public respuestasSolicitudes cambiarEstadoPlato (Integer id){
        Optional<Plato> plato = repo.findById(id);
        if(!plato.isPresent()){
            return respuestasSolicitudes.falta_plato;
        }
        boolean activo=plato.get().getActivo();
        plato.get().setActivo(!activo);
        repo.save(plato.get());
        return respuestasSolicitudes.plato_borrado;
    }
    

    public List<Plato> traerPlatos(){
        return repo.findByActivoTrue(); 
    }

    public Optional<Plato> traerPlato(Integer id){
        return repo.findById(id);
    }


    public enum respuestasSolicitudes{
        plato_creado,
        falta_plato,
        plato_actualizado,
        plato_borrado
    }

}
