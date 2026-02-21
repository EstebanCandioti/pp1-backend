package com.example.pp1.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.pp1.DTO.usuario.ActualizarUsuarioDTO;
import com.example.pp1.DTO.usuario.RegistrarUsuarioDTO;
import com.example.pp1.Entity.Usuario;
import com.example.pp1.Entity.UsuarioAsistencia;
import com.example.pp1.repository.UsuarioRepository;

@Service
public class UsuarioService {
 
    
    private final UsuarioRepository repo;

    public UsuarioService (UsuarioRepository repo){
        this.repo=repo;
    }

    public resultadoPeticiones estadoUsuario(Integer id){
        Optional<Usuario> usuario = repo.findById(id);
        if (usuario.isPresent()) {
            if (usuario.get().getActivo() == true) {
                usuario.get().setActivo(false);
                repo.save(usuario.get());
                return resultadoPeticiones.usuario_desactivado;
            } else {
                usuario.get().setActivo(true);
                repo.save(usuario.get());
                return resultadoPeticiones.usuario_activado;
            }
        } else {
            return resultadoPeticiones.falta_usuario;
        }
    }

    public resultadoPeticiones registrarUsuario(RegistrarUsuarioDTO usuario){
        Optional<Usuario> usu = repo.findByCorreoIgnoreCase(usuario.getEmail());
        if (usu.isPresent()) {
            return resultadoPeticiones.email_duplicado;
        }
        Usuario user = new Usuario();
        user.setActivo(true);
        user.setNombre(usuario.getNombre());
        user.setApellido(usuario.getApellido());
        user.setCorreo(usuario.getEmail());
        user.setDireccion(usuario.getDireccion());
        user.setEs_usuario_restaurante(usuario.getUsuarioRestaurante());
        user.setTelefono(usuario.getTelefono());
        user.setPassword(usuario.getPassword());
        List<UsuarioAsistencia> dias = new ArrayList<>();
        if (usuario.getDiasAsistencia() != null && !usuario.getDiasAsistencia().isEmpty()) {
            for (String dia : usuario.getDiasAsistencia()) {
                UsuarioAsistencia asistencia = new UsuarioAsistencia();
                asistencia.setDia(dia);
                asistencia.setUsuario(user);
                dias.add(asistencia);
            }
        } else {
            // acá devolvés falta de asistencia, según tu enum de resultado
            return resultadoPeticiones.falta_usuario; // o lo que tengas para "sin dias"
        }
        user.setDiasAsistencia(dias);
        repo.save(user);
        return resultadoPeticiones.ok;
    }

    public resultadoPeticiones actualizarUsuario(ActualizarUsuarioDTO usuario){
        Optional<Usuario> usu = repo.findById(usuario.getId());
        if (usu.isPresent()) {
            if(usu.get().getActivo()==false){
                return resultadoPeticiones.usuario_inactivo;
            }
            usu.get().setApellido(usuario.getApellido());
            usu.get().setNombre(usuario.getNombre());
            usu.get().setDireccion(usuario.getDireccion());
            usu.get().setTelefono(usuario.getTelefono());
            List<UsuarioAsistencia> diasActuales = usu.get().getDiasAsistencia();
            if(diasActuales == null){
                diasActuales= new ArrayList<>();
                usu.get().setDiasAsistencia(diasActuales);
            }else{
                diasActuales.clear();
            }

            for(String dia: usuario.getDiasAsistencia()){
                UsuarioAsistencia asistencia = new UsuarioAsistencia();
                asistencia.setDia(dia);
                asistencia.setUsuario(usu.get());
                diasActuales.add(asistencia);
            }
            repo.save(usu.get());
            return resultadoPeticiones.ok;
        } else {
            return resultadoPeticiones.falta_usuario;
        }
    }

    public List<Usuario> obtenerUsuarios(){
        return repo.findAll();
    }

    public Optional<Usuario> obtenerUsuario(Integer id){
        return repo.findById(id);
    }

    public Optional<Usuario> obtenerUsuarioPorEmail(String email){
        return repo.findByCorreoIgnoreCase(email);
    }

    public resultadoPeticiones login(String email, String password){
        Optional<Usuario> user = repo.findByCorreoIgnoreCase(email);
        if(user.isEmpty()){
            return resultadoPeticiones.falta_usuario;
        }
        if (user.get().getActivo() == false) {
            return resultadoPeticiones.usuario_inactivo;
        }
        if(user.get().getPassword().equals(password)){
            return resultadoPeticiones.logeado;
        }else{
            return  resultadoPeticiones.password_incorrecta;
        }
    }
    

    public enum resultadoPeticiones{
        ok,
        email_duplicado,
        falta_usuario,
        usuario_desactivado,
        usuario_activado,
        logeado,
        password_incorrecta,
        usuario_inactivo

    }
}
