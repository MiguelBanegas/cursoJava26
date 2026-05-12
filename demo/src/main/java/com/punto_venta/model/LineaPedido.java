package com.punto_venta.model;

public class LineaPedido {
    private Producto producto;
    private int cantidad;
    private double precioUnitarioVenta; // Precio del producto con IVA en el momento de la venta
    private double descuentoAplicado; // Descuento aplicado a esta línea (si es necesario)

    public LineaPedido(Producto producto, int cantidad) {
        if (producto == null || cantidad <= 0) {
            throw new IllegalArgumentException("Producto o cantidad inválida para la línea de pedido.");
        }
        this.producto = producto;
        this.cantidad = cantidad;
        // Capturamos el precio final (con IVA y descuento) en el momento de la venta
        this.precioUnitarioVenta = producto.precioConIva(); 
        this.descuentoAplicado = producto.descuentoPorProducto(); // Calculamos el descuento por producto
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad > 0) {
            this.cantidad = cantidad;
        } else {
            System.out.println("Advertencia: La cantidad debe ser mayor a cero.");
        }
    }

    public double getPrecioUnitarioVenta() {
        return precioUnitarioVenta;
    }

    public double getSubtotal() {
        return precioUnitarioVenta * cantidad;
    }
    public double getDescuentoAplicado() {
        return descuentoAplicado * cantidad; // Descuento total aplicado a esta línea
    }

}