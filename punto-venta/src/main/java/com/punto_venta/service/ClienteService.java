package com.punto_venta.service;

import com.punto_venta.exception.ClienteNotFoundException;
import com.punto_venta.model.Cliente;
import com.punto_venta.repository.ClienteRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> getAllClientes() {
        return clienteRepository.findByActivoTrue();
    }

    public Cliente getClienteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo");
        }
        return clienteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente con ID " + id + " no encontrado"));
    }

    public Cliente getClienteActivoById(Long id) {
        return getClienteById(id);
    }

    public Cliente getClienteByDni(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
        return clienteRepository.findByDniAndActivoTrue(dni)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente con DNI " + dni + " no encontrado"));
    }

    public Cliente addCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }

        if (cliente.getDni() == null || cliente.getDni().isBlank()) {
            throw new IllegalArgumentException("El DNI del cliente es obligatorio");
        }

        if (clienteRepository.existsByDni(cliente.getDni())) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con el DNI " + cliente.getDni());
        }

        return clienteRepository.save(cliente);
    }

    @SuppressWarnings("null")
    public Cliente updateCliente(@NonNull Long id, Cliente updatedCliente) {
        if (updatedCliente == null) {
            throw new IllegalArgumentException("Los datos de actualización no pueden ser nulos");
        }

        return clienteRepository.findByIdAndActivoTrue(id).map(existingCliente -> {
            // Validar y actualizar el nombre
            if (updatedCliente.getNombre() != null && !updatedCliente.getNombre().isBlank()) {
                existingCliente.setNombre(updatedCliente.getNombre());
            }

            // Validar y actualizar el apellido
            if (updatedCliente.getApellido() != null && !updatedCliente.getApellido().isBlank()) {
                existingCliente.setApellido(updatedCliente.getApellido());
            }

            // Validar y actualizar el DNI
            if (updatedCliente.getDni() != null && !updatedCliente.getDni().isBlank()) {
                // Si el DNI cambia, validamos que no esté en uso por otro cliente
                if (!existingCliente.getDni().equals(updatedCliente.getDni())) {
                    if (clienteRepository.existsByDni(updatedCliente.getDni())) {
                        throw new IllegalArgumentException("Ya existe otro cliente registrado con el DNI " + updatedCliente.getDni());
                    }
                    existingCliente.setDni(updatedCliente.getDni());
                }
            }

            // Actualizar campos opcionales
            if (updatedCliente.getEmail() != null) {
                existingCliente.setEmail(updatedCliente.getEmail().isBlank() ? null : updatedCliente.getEmail());
            }

            if (updatedCliente.getTelefono() != null) {
                existingCliente.setTelefono(updatedCliente.getTelefono().isBlank() ? null : updatedCliente.getTelefono());
            }

            if (updatedCliente.getDireccion() != null) {
                existingCliente.setDireccion(updatedCliente.getDireccion().isBlank() ? null : updatedCliente.getDireccion());
            }

            return clienteRepository.save(existingCliente);
        }).orElseThrow(() -> new ClienteNotFoundException("No se puede actualizar: Cliente con ID " + id + " no encontrado"));
    }

    public void deleteCliente(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID para eliminar no puede ser nulo");
        }
        Cliente cliente = getClienteById(id);
        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }
}
