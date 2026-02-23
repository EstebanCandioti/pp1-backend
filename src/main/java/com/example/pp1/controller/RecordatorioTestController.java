package com.example.pp1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pp1.Service.RecordatorioService;

/**
 * Controller SOLO PARA TESTING
 * 
 * Permite ejecutar los recordatorios manualmente sin esperar
 * al scheduler automático.
 */
@RestController
@RequestMapping("/test/recordatorios")
public class RecordatorioTestController {

    @Autowired
    private RecordatorioService recordatorioService;

    /**
     * Test: Ejecutar recordatorio de jueves manualmente
     * POST http://localhost:8080/test/recordatorios/jueves
     */
    @PostMapping("/jueves")
    public ResponseEntity<String> testRecordatorioJueves() {
        System.out.println("\n[TEST MANUAL] Ejecutando recordatorios JUEVES");
        
        try {
            recordatorioService.enviarRecordatoriosJueves();
            return ResponseEntity.ok("Recordatorios de jueves ejecutados exitosamente. Verificá los logs y las notificaciones en la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error al ejecutar recordatorios: " + e.getMessage());
        }
    }

    /**
     * Test: Ejecutar recordatorio de viernes manualmente
     * POST http://localhost:8080/test/recordatorios/viernes
     */
    @PostMapping("/viernes")
    public ResponseEntity<String> testRecordatorioViernes() {
        System.out.println("\n[TEST MANUAL] Ejecutando recordatorios VIERNES");
        
        try {
            recordatorioService.enviarRecordatoriosViernes();
            return ResponseEntity.ok("Recordatorios de viernes ejecutados exitosamente. Verificá los logs y las notificaciones en la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error al ejecutar recordatorios: " + e.getMessage());
        }
    }

    /**
     * Test: Ejecutar recordatorio de menús jueves manualmente
     * POST http://localhost:8080/test/recordatorios/menus-jueves
     */
    @PostMapping("/menus-jueves")
    public ResponseEntity<String> testRecordatoriosMenusJueves() {
        System.out.println("\n[TEST MANUAL] Ejecutando recordatorios MENÚS JUEVES");
        
        try {
            recordatorioService.enviarRecordatoriosMenusJueves();
            return ResponseEntity.ok("Recordatorios de menús (jueves) ejecutados exitosamente. Verificá los logs y las notificaciones en la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error al ejecutar recordatorios de menús: " + e.getMessage());
        }
    }

    /**
     * Test: Ejecutar recordatorio de menús viernes manualmente
     * POST http://localhost:8080/test/recordatorios/menus-viernes
     */
    @PostMapping("/menus-viernes")
    public ResponseEntity<String> testRecordatoriosMenusViernes() {
        System.out.println("\n[TEST MANUAL] Ejecutando recordatorios MENÚS VIERNES");
        
        try {
            recordatorioService.enviarRecordatoriosMenusViernes();
            return ResponseEntity.ok("Recordatorios de menús (viernes) ejecutados exitosamente. Verificá los logs y las notificaciones en la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error al ejecutar recordatorios de menús: " + e.getMessage());
        }
    }
}