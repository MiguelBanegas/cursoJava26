package com.punto_venta;

import com.punto_venta.model.Cliente;
import com.punto_venta.model.Pedido;
import com.punto_venta.model.Producto;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); 

        ArrayList<Producto> inventario = new ArrayList<>(); // Creamos un inventario de productos (lista dinamica)

        // Generamos 5 productos automáticamente (IDs del 1 al 5)
        for (int i = 1; i <= 5 ; i++) {
            inventario.add(new Producto(String.valueOf(i), "Producto " + i, 50, i * 10.0));
        }

        Cliente cliente1 = new Cliente("Juan Pérez", "juan.perez@email.com");
        Pedido miPedido = new Pedido(cliente1);

        System.out.println("=== BIENVENIDO AL SISTEMA DE VENTAS ===");
        
        while (true) {
            System.out.println("\n--- LISTA DE PRODUCTOS ---");
            for (Producto p : inventario) {
                System.out.printf("ID: %-2s | %-15s | Precio: $%7.2f | Stock: %d%n", 
                                 p.getId(), p.getNombre(), p.getPrecio(), p.getCantStock());
            }

            System.out.print("\nIngrese el ID del producto (o '0' para finalizar y ver ticket): ");
            String idElegido = scanner.nextLine();

            if (idElegido.equals("0")) {
                double ahorro = miPedido.calcularDescuentoTotal();
                System.out.printf("\nTotal de descuento por pago en efectivo es: $%.2f", ahorro);
                System.out.print("\n¿Desea aplicar el descuento por pago en efectivo? (s/n): ");
                idElegido = scanner.nextLine();
                // Seteamos en el pedido si aplica o no el descuento según la respuesta
                miPedido.setAplicaDescuento(idElegido.equalsIgnoreCase("s"));
                break; // Cerramos el pedido
            }

            // Buscar el producto en el inventario por ID
            Producto productoSeleccionado = null;
            for (Producto p : inventario) { // Recorremos el inventario para encontrar el producto con el ID ingresado
                if (p.getId().equals(idElegido)) {
                    productoSeleccionado = p;
                    break;
                }
            }

            if (productoSeleccionado != null) {
                System.out.print("Ingrese la cantidad: ");
                try {
                    int cantidad = Integer.parseInt(scanner.nextLine());
                    miPedido.agregarProducto(productoSeleccionado, cantidad);
                } catch (NumberFormatException e) {
                    System.out.println("Error: Por favor, ingrese un número válido para la cantidad.");
                }
            } else {
                System.out.println("Error: Producto con ID '" + idElegido + "' no encontrado.");
            }
        }

        miPedido.mostrarTicket();
        scanner.close();
    }

    public static void mostrarListadoProducto() {
        //Producto producto1 = new Producto("P001", "Laptop", 10, 999.99);
        //Producto producto2 = new Producto("P002", "Tablet", 5, 499.99);
        //productos.add(producto1);
        //productos.add(producto2);
        ArrayList<Producto> productos = new ArrayList<>();
        // Creamos 10 productos automáticamente
        for (int i = 1; i <= 3; i++) {
            productos.add(new Producto("P00" + i, "Producto genérico " + i, i * 2, i * 10.5));
        }

        // Acceder a un producto específico por su índice (ejemplo: el primero)
        //System.out.println("--- MOSTRANDO SOLO EL PRIMER PRODUCTO (Índice 0) ---");
        //productos.get(0).mostrarInfoProducto();
        //System.out.println("--------------------------------------------------\n");
        
        for (Producto producto : productos) {
            //producto.mostrarInfoProducto();
            System.out.println(producto.toString());
            //System.out.println("Precio con IVA: $" + producto.precioConIva());
            System.out.println();
        }
    }
    public static void mostrarListadoClientes() {
        ArrayList<Cliente> clientes = new ArrayList<>();
        // Creamos 10 clientes automáticamente
        for (int i = 1; i <= 5; i++) {
            clientes.add(new Cliente("Cliente " + i, "cliente" + i + "@example.com"));
        }

       /*  for (Cliente cliente : clientes) {
            System.out.println("Nombre: " + cliente.getNombre());
            System.out.println("Email: " + cliente.getEmail());
            System.out.println();
        } */
    }
    
}