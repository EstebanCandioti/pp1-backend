package com.example.pp1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pp1.Entity.Notificacion;

public interface NotificacionRepository  extends  JpaRepository<Notificacion, Integer>{
    // Listar notificaciones de un usuario, más nuevas primero
    List<Notificacion> findByUsuario_IdUsuarioOrderByFechaEnvioDesc(Integer idUsuario);

    // Eliminar todas las notificaciones de un usuario
    void deleteByUsuario_IdUsuario(Integer idUsuario);
}
