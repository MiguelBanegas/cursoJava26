package com.punto_venta.controller;

//import com.punto_venta.exception.ProductNotFoundException;
import com.punto_venta.model.Product;
import com.punto_venta.model.ApiResponse;
import com.punto_venta.service.ProductService;
import com.punto_venta.dto.StockUpdateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController // Indica que esta clase es un controlador REST y que los métodos devolverán
                // respuestas JSON
@RequestMapping("/products") // Define la ruta base para todos los endpoints de este controlador
public class ProductController {

    private final ProductService productService; // Inyección de dependencia del servicio que maneja la lógica de
                                                 // negocio relacionada con los productos

    public ProductController(ProductService productService) { // Constructor que recibe el ProductService a través de la
                                                              // inyección de dependencias de Spring
        this.productService = productService;
    }

    @GetMapping // Maneja solicitudes GET a "/products" para obtener la lista de todos los
                // productos
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product newProduct) {
        Product createdProduct = productService.addProduct(newProduct);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Devuelve el producto creado con un estado
                                                                         // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable @NonNull Long id, @RequestBody Product product) { // Maneja
                                                                                                                 // solicitudes
                                                                                                                 // PUT
                                                                                                                 // a
                                                                                                                 // "/products/{id}"
                                                                                                                 // para
                                                                                                                 // actualizar
                                                                                                                 // un
                                                                                                                 // producto
                                                                                                                 // existente
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct); // Devuelve el producto actualizado con un estado 200 OK
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        ApiResponse response = new ApiResponse("Producto con ID " + id + " eliminado exitosamente",
                HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable @NonNull Long id, @Valid @RequestBody StockUpdateDTO dto) {
        Product updatedProduct = productService.actualizarStock(id, dto);
        return ResponseEntity.ok(updatedProduct);
    }
}
