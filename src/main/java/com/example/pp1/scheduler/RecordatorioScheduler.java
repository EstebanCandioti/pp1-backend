package com.example.pp1.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.pp1.Service.RecordatorioService;

/**
 * Scheduler para enviar recordatorios automáticos de pedidos
 * 
 * Ejecuta tareas programadas:
 * - Jueves 9:00 AM: Recordatorio general
 * - Viernes 9:00 AM: Recordatorio de urgencia
 */
@Component
public class RecordatorioScheduler {

    private final RecordatorioService recordatorioService;

    public RecordatorioScheduler(RecordatorioService recordatorioService) {
        this.recordatorioService = recordatorioService;
    }

    /**
     * Ejecuta todos los JUEVES a las 9:00 AM
     * Cron: "segundo minuto hora día mes día-semana"
     * - 0 0 9 * * THU = A las 9:00:00 AM todos los jueves
     */
    @Scheduled(cron = "0 0 9 * * THU", zone = "America/Argentina/Buenos_Aires")
    public void recordatorioJueves() {
        System.out.println("\n==========================================");
        System.out.println("SCHEDULER: Ejecutando recordatorios JUEVES");
        System.out.println("==========================================\n");
        
        try {
            recordatorioService.enviarRecordatoriosJueves();
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO en recordatorios jueves: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ejecuta todos los VIERNES a las 9:00 AM
     * Cron: "segundo minuto hora día mes día-semana"
     * - 0 0 9 * * FRI = A las 9:00:00 AM todos los viernes
     */
    @Scheduled(cron = "0 0 9 * * FRI", zone = "America/Argentina/Buenos_Aires")
    public void recordatorioViernes() {
        System.out.println("\n==========================================");
        System.out.println("SCHEDULER: Ejecutando recordatorios VIERNES (URGENTE)");
        System.out.println("==========================================\n");
        
        try {
            recordatorioService.enviarRecordatoriosViernes();
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO en recordatorios viernes: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Scheduled(cron = "0 */5 * * * *")
    public void testRecordatoriosCada5Minutos() {
       System.out.println("\n========== TEST: Recordatorios cada 5 min ==========");
       try {
          recordatorioService.enviarRecordatoriosJueves();
       } catch (Exception e) {
            System.err.println("ERROR en test de recordatorios: " + e.getMessage());
         }
     }
}