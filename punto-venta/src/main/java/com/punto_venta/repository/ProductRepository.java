package com.punto_venta.repository;

import com.punto_venta.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> { // Interfaz que extiende JpaRepository para proporcionar métodos CRUD para la entidad Product. El primer parámetro es la clase de la entidad y el segundo es el tipo del ID.
    List<Product> findByActivoTrue(); // Devuelve solo los productos activos (no eliminados lógicamente)
    Optional<Product> findByIdAndActivoTrue(Long id); // Busca un producto activo por su ID
}
