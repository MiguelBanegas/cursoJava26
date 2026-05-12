package com.punto_venta.model;

import java.util.ArrayList;

public class Pedido {
    private ArrayList<LineaPedido> lineasPedido; // Ahora almacenamos LineaPedido
    private Cliente cliente;
    private boolean aplicaDescuento; // Flag para controlar si se aplica el descuento

    public Pedido(Cliente cliente) {
        this.cliente = cliente;
        this.lineasPedido = new ArrayList<>(); // Inicializamos la lista de líneas de pedido
        this.aplicaDescuento = false; // Por defecto no hay descuento
    }

    public void setAplicaDescuento(boolean aplicaDescuento) {
        this.aplicaDescuento = aplicaDescuento;
    }

    // Método para agregar un producto con una cantidad específica
    public void agregarProducto(Producto producto, int cantidad) {
        if (producto == null || cantidad <= 0) {
            System.out.println("Error: Producto inválido o cantidad no válida (debe ser > 0).");
            return;
        }

        // Verificar si hay suficiente stock antes de agregar
        if (producto.getCantStock() < cantidad) {
            System.out.println("Error: No hay suficiente stock para '" + producto.getNombre() + "'. Stock disponible: " + producto.getCantStock());
            return;
        }

        // Buscar si el producto ya existe en alguna línea del pedido
        for (LineaPedido linea : lineasPedido) {
            if (linea.getProducto().getId().equals(producto.getId())) {
                // Si el producto ya está en el pedido, actualizamos la cantidad de esa línea
                producto.descontarStock(cantidad); 
                linea.setCantidad(linea.getCantidad() + cantidad);
                return;
            }
        }

        // Si el producto no está en el pedido, creamos una nueva línea
        producto.descontarStock(cantidad);
        lineasPedido.add(new LineaPedido(producto, cantidad));
    }

    public double getMontoTotal() {
        double total = 0.0;
        for (LineaPedido linea : lineasPedido) { // Sumamos el subtotal de cada línea de pedido
            total += linea.getSubtotal(); 
        }
        
        if (aplicaDescuento) {
            total -= calcularDescuentoTotal();
        }
        
        return total;
    }

    public int calcularCantidadTotal() {
        int totalArticulos = 0;
        for (LineaPedido linea : lineasPedido) {
            totalArticulos += linea.getCantidad();
        }
        return totalArticulos;
    }

    public void mostrarTicket() {
        System.out.println("\n========================================");
        System.out.println("           TICKET DE VENTA            ");
        System.out.println("========================================");
        System.out.println("Cliente: " + cliente.getNombre());
        System.out.println("Email:   " + cliente.getEmail());
        System.out.println("----------------------------------------");
        System.out.printf("%-20s %5s %8s%n", "Producto", "Cant.", "Subtotal");
        System.out.println("----------------------------------------");
        for (LineaPedido linea : lineasPedido) {
            System.out.printf("%-20s %5d $%8.2f%n", 
                              linea.getProducto().getNombre(), 
                              linea.getCantidad(), 
                              linea.getSubtotal());
        }
        System.out.println("----------------------------------------");
        System.out.printf("CANTIDAD TOTAL ARTÍCULOS: %13d%n", calcularCantidadTotal());
        
        if (aplicaDescuento) {
            System.out.printf("DESCUENTO TOTAL APLICADO: $%8.2f%n", calcularDescuentoTotal());
        } else {
            System.out.println("DESCUENTO TOTAL APLICADO: $    0.00");
        }
        
        System.out.printf("TOTAL A PAGAR:            $%8.2f%n", getMontoTotal());
        System.out.println("========================================\n");
    }

    public double calcularDescuentoTotal() {
        double descuentoTotal = 0.0;
        for (LineaPedido linea : lineasPedido) { 
            descuentoTotal += linea.getDescuentoAplicado(); // Sumamos el descuento aplicado de cada línea
        }
        return descuentoTotal;
    }
}
