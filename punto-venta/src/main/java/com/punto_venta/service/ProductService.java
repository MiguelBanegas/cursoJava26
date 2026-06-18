package com.punto_venta.service;

import com.punto_venta.exception.ProductNotFoundException;
import com.punto_venta.model.Product;
import com.punto_venta.repository.ProductRepository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
//import java.util.Optional;

@Service // Indica que esta clase es un servicio de Spring, lo que la hace elegible para la inyección de dependencias y para contener la lógica de negocio relacionada con los productos
public class ProductService {
    
    private final ProductRepository productRepository; // Inyección de dependencia del repositorio que maneja la persistencia de los productos en la base de datos
    private final CategoriaService categoriaService;

    public ProductService(ProductRepository productRepository, CategoriaService categoriaService) {
        this.productRepository = productRepository;
        this.categoriaService = categoriaService;
    }

    public List<Product> getAllProducts() { // Método que devuelve una lista de todos los productos almacenados en la base de datos utilizando el método findAll() del repositorio
        return productRepository.findAll();
    }

    public Product getProductById(Long id) { // Método que devuelve un producto específico por su ID. 
                                            // Si el producto no existe, lanza una excepción ProductNotFoundException.
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto con ID " + id + " no encontrado"));
    }

    public Product addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }

        if (product.getCategoria() == null || product.getCategoria().getId() == null) {
            throw new IllegalArgumentException("El producto debe tener una categoría válida con un ID");
        }

        // Validamos que la categoría exista antes de guardar el producto
        product.setCategoria(categoriaService.getCategoriaById(product.getCategoria().getId()));

        return productRepository.save(product);
    }

    @SuppressWarnings("null")
	public Product updateProduct(@NonNull Long id, Product updatedProduct) {
        if (updatedProduct == null) {
            throw new IllegalArgumentException("Los datos de actualización no pueden ser nulos");
        }

        return productRepository.findById(id).map(existingProduct -> {
            // Solo actualizamos el nombre si se envió y no está en blanco
            if (updatedProduct.getName() != null && !updatedProduct.getName().isBlank()) {
                existingProduct.setName(updatedProduct.getName());
            }
            
            // Solo actualizamos el precio si se envió
            if (updatedProduct.getPrice() != null) {
                if (updatedProduct.getPrice() <= 0) {
                    throw new IllegalArgumentException("El precio debe ser un valor positivo mayor a cero");
                }
                existingProduct.setPrice(updatedProduct.getPrice());
            }
            
            // Solo actualizamos la categoría si se envió y tiene un ID válido
            if (updatedProduct.getCategoria() != null && updatedProduct.getCategoria().getId() != null) {
                existingProduct.setCategoria(categoriaService.getCategoriaById(updatedProduct.getCategoria().getId()));
            }
            
            return productRepository.save(existingProduct);
        }).orElseThrow(() -> new ProductNotFoundException("No se puede actualizar: Producto con ID " + id + " no encontrado"));
    }

    public void deleteProduct(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID para eliminar no puede ser nulo");
        }
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Producto con ID " + id + " no encontrado");
        }
        productRepository.deleteById(id);
    }
}
