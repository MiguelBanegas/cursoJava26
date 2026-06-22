package com.punto_venta.controller;

import com.punto_venta.model.Cliente;
import com.punto_venta.model.ApiResponse;
import com.punto_venta.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> getAllClientes() {
        List<Cliente> clientes = clienteService.getAllClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        Cliente cliente = clienteService.getClienteById(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Cliente> getClienteByDni(@PathVariable String dni) {
        Cliente cliente = clienteService.getClienteByDni(dni);
        return ResponseEntity.ok(cliente);
    }

    @PostMapping
    public ResponseEntity<Cliente> addCliente(@Valid @RequestBody Cliente newCliente) {
        Cliente createdCliente = clienteService.addCliente(newCliente);
        return new ResponseEntity<>(createdCliente, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable @NonNull Long id, @RequestBody Cliente cliente) {
        Cliente updatedCliente = clienteService.updateCliente(id, cliente);
        return ResponseEntity.ok(updatedCliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
        ApiResponse response = new ApiResponse("Cliente con ID " + id + " eliminado exitosamente", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
