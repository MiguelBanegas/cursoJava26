package com.punto_venta.service;

import com.punto_venta.model.Categoria;
import com.punto_venta.repository.CategoriaRepository;
import com.punto_venta.exception.CategoriaNotFoundException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.util.List;

@Service 
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
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
    
    public void deleteCategoria(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID para eliminar no puede ser nulo");
        }
        if (!categoriaRepository.existsById(id)) {
            throw new CategoriaNotFoundException("Categoría no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }
    
    public boolean existsCategoria(@NonNull Long id) {
        return categoriaRepository.existsById(id);
    }
}
