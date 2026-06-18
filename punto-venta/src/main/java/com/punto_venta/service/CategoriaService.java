package com.punto_venta.service;

import com.punto_venta.model.Categoria;
import com.punto_venta.model.Product;
import com.punto_venta.repository.CategoriaRepository;
import com.punto_venta.repository.ProductRepository;
import com.punto_venta.exception.CategoriaNotFoundException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service 
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final ProductRepository productRepository;
    
    // Constructor que recibe las dependencias del repositorio 
    // de categorías y productos a través de la inyección de dependencias de Spring
    public CategoriaService(CategoriaRepository categoriaRepository, ProductRepository productRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productRepository = productRepository;
    }
    
    public List<Categoria> getAllCategorias() {
        return categoriaRepository.findAll();
    }
    
    public Categoria getCategoriaById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la categoría no puede ser nulo");
        }
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoría no encontrada con ID: " + id));
    }
    
    public Categoria saveCategoria(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        return categoriaRepository.save(categoria);
    }
    
    @SuppressWarnings("null")
    public Categoria updateCategoria(@NonNull Long id, Categoria categoriaDetails) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoría no encontrada con ID: " + id));
        
        if (categoriaDetails.getNombre() != null && !categoriaDetails.getNombre().isBlank()) {
            categoria.setNombre(categoriaDetails.getNombre());
        }
        
        if (categoriaDetails.getDescripcion() != null) {
            categoria.setDescripcion(categoriaDetails.getDescripcion());
        }

        return categoriaRepository.save(categoria);
    }
    
    @Transactional
    public void deleteCategoria(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID para eliminar no puede ser nulo");
        }
        // La categoría con ID 1 es la categoría "General" y no se puede eliminar
        if (id == 1L) {
            throw new IllegalArgumentException("La categoría 'General' (ID 1) es obligatoria y no puede ser eliminada");
        }

        Categoria categoriaToDelete = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoría no encontrada con ID: " + id));

        Categoria categoriaGeneral = categoriaRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Error: Debe existir una categoría con ID 1 (General) antes de eliminar otras."));

        // Reasignar productos a la categoría General
        List<Product> productos = categoriaToDelete.getProductos();
        if (!productos.isEmpty()) {
            productos.forEach(p -> p.setCategoria(categoriaGeneral));
            productRepository.saveAll(productos);
        }

        categoriaRepository.delete(categoriaToDelete);
    }
    
    public boolean existsCategoria(@NonNull Long id) {
        return categoriaRepository.existsById(id);
    }
}
