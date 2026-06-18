package com.punto_venta.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import com.punto_venta.model.ApiResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // Indica que esta clase manejará excepciones de forma global para todos los controladores
public class GlobalExceptionHandler { // Clase para manejar excepciones de forma global en la aplicación, proporcionando respuestas consistentes y detalladas para los errores que puedan ocurrir en los controladores REST

    @ExceptionHandler(ProductNotFoundException.class) // Maneja la excepción personalizada de producto no encontrado
    public ResponseEntity<ApiResponse> handleProductNotFound(ProductNotFoundException ex) {
        ApiResponse response = new ApiResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoriaNotFoundException.class) // Maneja la excepción personalizada de categoría no encontrada
    public ResponseEntity<ApiResponse> handleCategoriaNotFound(CategoriaNotFoundException ex) {
        ApiResponse response = new ApiResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class) // Maneja excepciones de argumentos ilegales, como datos nulos o inválidos
    public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ApiResponse response = new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // Maneja excepciones de validación de argumentos
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse response = new ApiResponse("Error de validación en los campos", HttpStatus.BAD_REQUEST.value(), errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class) // Maneja excepciones de lectura de mensajes HTTP, como JSON mal formado o tipos de datos incorrectos
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String errorMessage = "Error de lectura en el JSON";
        
        // Si el error es por un tipo de dato incorrecto (ej: enviar texto en un número)
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();
            String fieldName = ife.getPath().get(0).getFieldName();
            errorMessage = String.format("Error de tipo: El campo '%s' debe ser de tipo %s", 
                fieldName, ife.getTargetType().getSimpleName());
        } else if (ex.getMessage() != null && ex.getMessage().contains("JSON parse error")) {
            errorMessage = "Error de sintaxis: El JSON enviado no es válido (revisa comas o llaves).";
        }
        
        ApiResponse response = new ApiResponse(errorMessage, HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings("null")
	@ExceptionHandler(MethodArgumentTypeMismatchException.class) // Maneja cuando un parámetro de ruta tiene un tipo incorrecto (ej: enviar texto en lugar de Long)
    public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido";
        
        String message = String.format("El parámetro '%s' debe ser de tipo %s, pero recibió: %s", 
            ex.getName(), requiredType, ex.getValue());
        ApiResponse response = new ApiResponse(message, HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class) // Maneja cuando no se encuentra la ruta solicitada
    public ResponseEntity<ApiResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        ApiResponse response = new ApiResponse("La ruta solicitada no existe: " + ex.getRequestURL(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class) // Maneja cualquier otra excepción no controlada
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        String message = "Ha ocurrido un error inesperado: " + ex.getMessage();
        ApiResponse response = new ApiResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getClass().getSimpleName());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
