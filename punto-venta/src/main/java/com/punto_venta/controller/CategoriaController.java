package com.punto_venta.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.punto_venta.model.Categoria;
import com.punto_venta.model.ApiResponse;
import com.punto_venta.service.CategoriaService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;
    
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }
    
    @GetMapping
    public ResponseEntity<List<Categoria>> getAllCategorias() {
        List<Categoria> categorias = categoriaService.getAllCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoriaById(@PathVariable Long id) {
        Categoria categoria = categoriaService.getCategoriaById(id);
        return ResponseEntity.ok(categoria);
    }
    
    @PostMapping
    public ResponseEntity<Categoria> createCategoria(@RequestBody Categoria categoria) {
        Categoria savedCategoria = categoriaService.saveCategoria(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoria);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> updateCategoria(@PathVariable Long id, @RequestBody Categoria categoriaDetails) {
        Categoria updatedCategoria = categoriaService.updateCategoria(id, categoriaDetails);
        return ResponseEntity.ok(updatedCategoria);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategoria(@PathVariable Long id) {
        categoriaService.deleteCategoria(id);
        ApiResponse response = new ApiResponse("Categoría eliminada correctamente", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
