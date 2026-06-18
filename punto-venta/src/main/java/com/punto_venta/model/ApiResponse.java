package com.punto_venta.model;

import java.time.LocalDateTime;
// Clase para representar una respuesta de API con un mensaje, un código de estado, una marca de tiempo y datos opcionales. Esta clase se utiliza para estandarizar las respuestas de la API y proporcionar información adicional sobre los errores o resultados de las operaciones.
public class ApiResponse {
    
    private String message;
    private LocalDateTime timestamp;
    private int status;
    private Object data;

    public ApiResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(String message, int status, Object data) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
