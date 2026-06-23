package com.punto_venta.repository;

import com.punto_venta.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDni(String dni);                        // Para validar duplicado de DNI (incluye inactivos)
    boolean existsByDni(String dni);                                // Para validar duplicado de DNI (incluye inactivos)
    List<Cliente> findByActivoTrue();                               // Devuelve solo los clientes activos
    Optional<Cliente> findByIdAndActivoTrue(Long id);              // Busca un cliente activo por ID
    Optional<Cliente> findByDniAndActivoTrue(String dni);          // Busca un cliente activo por DNI
}
