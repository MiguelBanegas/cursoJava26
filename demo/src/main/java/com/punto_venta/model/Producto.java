package com.punto_venta.model;

public class Producto {
    private String id;
    private String nombre;
    private int cantStock;
    private double precio;
    private static double iva = 0.21; // Impuesto fijo del 21%
    private static double descuentoFijo = 0.10; // Descuento fijo del 10%

    public String getId() {
        return id;
    } 
    
    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        if (precio >= 0) {
            this.precio = precio;
        }
    }

    public String getNombre() {
        return nombre;
    }
     
    public Producto(String id, String nombre, int cantStock, double precio) {
        this.id = id;   this.nombre = nombre;   this.cantStock = cantStock;
        
        setPrecio(precio);
    }


    public static double impuesto(double precio){
        return precio * iva;
    }

    public double precioConIva() {
        return this.precio + impuesto(this.precio);
    }
    public double descuentoPorProducto() {
        return (this.precioConIva() * descuentoFijo);
    }

    public int getCantStock() {
        return cantStock;
    }
    public void setCantStock(int cantStock) {
        if (cantStock >= 0) {
            this.cantStock = cantStock;
        }
    }
    public int descontarStock(int cantidad) {
        if (cantidad > 0 && cantidad <= this.cantStock) {
            this.cantStock -= cantidad;
            return cantidad;
        } else {
            return 0; // No se puede descontar una cantidad inválida
        }
    }   
    public int agregarStock(int cantidad) {
        if (cantidad > 0) {
            this.cantStock += cantidad;
            return cantidad;
        } else {
            return 0; // No se puede agregar una cantidad inválida
        }
    }

    @Override
    public String toString() {
        return String.format("ID: %s | %-15s | Precio: $%7.2f | Stock: %d", 
                             id, nombre, precio, cantStock);
    }
}
