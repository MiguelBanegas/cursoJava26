package com.punto_venta.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockUpdateDTO {

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;

    @NotNull(message = "La operación es obligatoria")
    private StockOperation operacion;

    public StockUpdateDTO() {
    }

    public StockUpdateDTO(Integer cantidad, StockOperation operacion) {
        this.cantidad = cantidad;
        this.operacion = operacion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public StockOperation getOperacion() {
        return operacion;
    }

    public void setOperacion(StockOperation operacion) {
        this.operacion = operacion;
    }
}
