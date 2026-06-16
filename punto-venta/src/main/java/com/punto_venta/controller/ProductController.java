package com.punto_venta.controller;

import com.punto_venta.exception.ProductNotFoundException;
import com.punto_venta.model.Product;
import com.punto_venta.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // Indica que esta clase es un controlador REST y que los métodos devolverán respuestas JSON
@RequestMapping("/api/products") // Define la ruta base para todos los endpoints de este controlador, en este caso "/api/products"
public class ProductController {

    private final ProductService productService; // Inyección de dependencia del servicio que maneja la lógica de negocio relacionada con los productos

    public ProductController(ProductService productService) { // Constructor que recibe el ProductService a través de la inyección de dependencias de Spring
        this.productService = productService;
    }

    @GetMapping // Maneja solicitudes GET a "/api/products" para obtener la lista de todos los productos
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto con ID " + id + " no encontrado")); // Si el producto no se encuentra, lanza una excepción personalizada que será manejada por GlobalExceptionHandler
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product newProduct) {
        Product createdProduct = productService.addProduct(newProduct);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Devuelve el producto creado con un estado 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) { // Maneja solicitudes PUT a "/api/products/{id}" para actualizar un producto existente
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct); // Devuelve el producto actualizado con un estado 200 OK
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Producto con ID " + id + " eliminado exitosamente");
        return ResponseEntity.ok(response); // Devuelve una respuesta con un mensaje de éxito y la hora de eliminación
    }
}
