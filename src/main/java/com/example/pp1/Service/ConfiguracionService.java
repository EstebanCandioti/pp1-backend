package com.example.pp1.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.example.pp1.Entity.Configuracion;
import com.example.pp1.repository.ConfiguracionRepository;

import jakarta.transaction.Transactional;

@Service
public class ConfiguracionService {

    public enum RespuestaPeticiones {
        ok,
        falta_configuracion,
        horario_invalido,
        fecha_invalida
    }

    private static final Pattern HORARIO_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");
    private static final int CONFIG_ID = 1;

    private final ConfiguracionRepository repo;

    public ConfiguracionService(ConfiguracionRepository repo) {
        this.repo = repo;
    }

    /**
     * Devuelve la configuración actual. Si no existe, crea una por defecto.
     */
    public Configuracion obtenerConfiguracion() {
        return repo.findById(CONFIG_ID).orElseGet(() -> {
            Configuracion config = new Configuracion("10:30");
            return repo.save(config);
        });
    }

    /**
     * Valida el formato HH:MM y actualiza el horario límite.
     */
    @Transactional
    public RespuestaPeticiones actualizarHorarioLimite(String nuevoHorario) {
        if (nuevoHorario == null || !HORARIO_PATTERN.matcher(nuevoHorario).matches()) {
            return RespuestaPeticiones.horario_invalido;
        }
        Configuracion config = obtenerConfiguracion();
        config.setHorarioLimite(nuevoHorario);
        repo.save(config);
        return RespuestaPeticiones.ok;
    }

    /**
     * Agrega una fecha a la lista de feriados. Rechaza null.
     */
    @Transactional
    public RespuestaPeticiones agregarFeriado(LocalDate fecha) {
        if (fecha == null) {
            return RespuestaPeticiones.fecha_invalida;
        }
        Configuracion config = obtenerConfiguracion();
        if (!config.getFeriados().contains(fecha)) {
            config.getFeriados().add(fecha);
            repo.save(config);
        }
        return RespuestaPeticiones.ok;
    }

    /**
     * Elimina una fecha de la lista de feriados.
     */
    @Transactional
    public RespuestaPeticiones eliminarFeriado(LocalDate fecha) {
        if (fecha == null) {
            return RespuestaPeticiones.fecha_invalida;
        }
        Configuracion config = obtenerConfiguracion();
        config.getFeriados().remove(fecha);
        repo.save(config);
        return RespuestaPeticiones.ok;
    }

    /**
     * Reemplaza toda la lista de feriados.
     */
    @Transactional
    public RespuestaPeticiones actualizarFeriados(List<LocalDate> feriados) {
        if (feriados == null) {
            return RespuestaPeticiones.fecha_invalida;
        }
        Configuracion config = obtenerConfiguracion();
        config.setFeriados(feriados);
        repo.save(config);
        return RespuestaPeticiones.ok;
    }
}