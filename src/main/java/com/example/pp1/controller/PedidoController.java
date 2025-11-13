package com.example.pp1.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pp1.Entity.Pedido;
import com.example.pp1.repository.PedidoRepository;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    @Autowired
    private PedidoRepository repo;

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPedido(@PathVariable Integer id) {
        Optional<Pedido> pedido = repo.findById(id);
        if (repo.findById(id).isPresent()) {
            return ResponseEntity.ok(pedido.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/lista")
    public List<Pedido> obtenerListaPedidos() {
        return repo.findAll();
    }

    @GetMapping("/lista/{id}")
    public List<Pedido> getMethodName(@PathVariable Integer id) {
        return repo.findById_usuario(id);
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarPedido(@RequestBody Pedido pedido) {
        Optional<Pedido> ped = repo.findById(pedido.getId_pedido());
        if (ped.isPresent()) {
            repo.save(pedido);
            return ResponseEntity.ok("El pedido fue guardado");
        }
        return ResponseEntity.notFound().build();
    }
}
