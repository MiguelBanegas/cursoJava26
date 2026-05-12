# Sistema de Punto de Venta (Java)

Aplicación de consola en Java para simular un flujo básico de venta:
- listado de productos,
- selección por ID,
- carga de cantidades,
- control de stock,
- cálculo de IVA y descuento,
- emisión de ticket final.

## Tecnologías

- Java 17
- Maven

## Estructura del Proyecto

- `src/main/java/com/punto_venta/Main.java`
  - Punto de entrada.
  - Inicializa inventario y cliente.
  - Ejecuta el loop interactivo por consola.
- `src/main/java/com/punto_venta/model/Producto.java`
  - Modelo de producto.
  - Maneja precio base, stock, IVA y descuento por producto.
- `src/main/java/com/punto_venta/model/LineaPedido.java`
  - Representa una línea del pedido (producto + cantidad).
  - Guarda precio unitario al momento de venta y subtotal.
- `src/main/java/com/punto_venta/model/Pedido.java`
  - Agrupa líneas del pedido asociadas a un cliente.
  - Agrega productos, valida stock, calcula total y muestra ticket.
- `src/main/java/com/punto_venta/model/Cliente.java`
  - Datos básicos del cliente (nombre y email).

## Flujo de uso

1. El sistema crea un inventario inicial de 5 productos (`ID` 1 al 5), todos con stock 50.
2. Muestra los productos por consola.
3. El usuario ingresa:
   - `ID` del producto para agregarlo al pedido, o
   - `0` para finalizar.
4. Si ingresa un producto válido, solicita cantidad:
   - valida que sea número,
   - valida stock disponible,
   - descuenta stock y agrega/actualiza la línea en el pedido.
5. Al finalizar (`0`), muestra el ahorro posible por pago en efectivo y pregunta si aplicar descuento.
6. Emite ticket con:
   - cliente,
   - detalle por línea,
   - cantidad total de artículos,
   - descuento aplicado,
   - total final.

## Reglas de negocio implementadas

- IVA fijo: `21%` (`Producto.iva`).
- Descuento fijo por producto: `10%` sobre precio con IVA (`Producto.descuentoFijo`).
- El descuento total solo se resta al total si el usuario confirma pago en efectivo.
- No se pueden agregar cantidades menores o iguales a cero.
- No se permite vender por encima del stock disponible.

## Cálculo del total

- Subtotal por línea: `precioUnitarioVenta * cantidad`.
- Total bruto: suma de subtotales.
- Descuento total: suma de descuentos por línea.
- Total final:
  - con descuento: `totalBruto - descuentoTotal`
  - sin descuento: `totalBruto`



## Capturas en tiempo de ejecución

Se incluyen dos ejecuciones reales de consola:

- [Ejecución aplicando descuento](./capturas/ejecucion-con-descuento.txt)
- [Ejecución sin descuento](./capturas/ejecucion-sin-descuento.txt)
