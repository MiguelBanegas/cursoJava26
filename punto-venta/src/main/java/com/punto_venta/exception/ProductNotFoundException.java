package com.punto_venta.exception;

public class ProductNotFoundException extends RuntimeException { // Excepción personalizada para cuando un producto no es encontrado
    public ProductNotFoundException(String message) {
        super(message); // Llama al constructor de RuntimeException con el mensaje personalizado
    }
}
