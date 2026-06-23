package com.punto_venta.controller;

import com.punto_venta.dto.PedidoCreateRequestDTO;
import com.punto_venta.dto.PedidoResponseDTO;
import com.punto_venta.model.PedidoEstado;
import com.punto_venta.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> createPedido(@Valid @RequestBody PedidoCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.createPedido(request));
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<PedidoResponseDTO> confirmarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.confirmarPedido(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> getPedidos() {
        return ResponseEntity.ok(pedidoService.getPedidos());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosByEstado(@PathVariable PedidoEstado estado) {
        return ResponseEntity.ok(pedidoService.getPedidosByEstado(estado));
    }

    @GetMapping("/rango")
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosByRangoFecha(@RequestParam LocalDateTime desde, @RequestParam LocalDateTime hasta) {
        return ResponseEntity.ok(pedidoService.getPedidosByRangoFecha(desde, hasta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> getPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.getPedido(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.getPedidosByCliente(clienteId));
    }
}
