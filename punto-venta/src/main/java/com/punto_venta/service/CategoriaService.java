package com.punto_venta.service;

import com.punto_venta.model.Categoria;
import com.punto_venta.repository.CategoriaRepository;
import com.punto_venta.exception.CategoriaNotFoundException;
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
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoría no encontrada con ID: " + id));
    }
    
    public Categoria saveCategoria(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        return categoriaRepository.save(categoria);
    }
    
    public Categoria updateCategoria(Long id, Categoria categoriaDetails) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoría no encontrada con ID: " + id));
        
        if (categoriaDetails.getNombre() != null && !categoriaDetails.getNombre().isBlank()) {
            categoria.setNombre(categoriaDetails.getNombre());
        }
        return categoriaRepository.save(categoria);
    }
    
    public void deleteCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new CategoriaNotFoundException("Categoría no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }
    
    public boolean existsCategoria(Long id) {
        return categoriaRepository.existsById(id);
    }
}
