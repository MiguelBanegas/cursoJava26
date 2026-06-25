# API de Punto de Venta

Esta es una API RESTful para gestionar productos, clientes y pedidos en un sistema de punto de venta. Permite realizar operaciones CRUD sobre productos (con sus categorías), clientes y pedidos, con borrado lógico para productos y clientes.

## 🚀 Características

- **Gestión de Productos**:
  - Creación de nuevos productos con nombre, precio y categoría.
  - Consulta de todos los productos o de un producto específico por su ID.
  - Actualización parcial de productos (nombre, precio, categoría).
  - Eliminación lógica de productos mediante soft delete.
  - Control de stock con operaciones de Agregar, Restar y Ajustar.
- **Gestión de Categorías**:
  - CRUD completo bajo `/categorias`.
  - Cada producto está asociado a una categoría existente.
  - Categoría `GENERAL` (ID `1`) creada automáticamente si la tabla está vacía al iniciar.
  - Al eliminar una categoría, sus productos pasan a `GENERAL`. La categoría `GENERAL` no puede eliminarse.
- **Gestión de Clientes**:
  - Creación de nuevos clientes con nombre, apellido y DNI único.
  - Consulta de todos los clientes, por ID o por DNI.
  - Actualización parcial de la información de los clientes (nombre, apellido, email, teléfono, dirección).
  - Eliminación lógica de clientes mediante soft delete.
- **Gestión de Pedidos**:
  - Creación de pedidos asociados a un cliente con varios productos.
  - Confirmación de pedidos con descuento de stock.
  - Cancelación de pedidos confirmados con restitución de stock.
  - Consulta de pedidos por ID, por cliente, por estado y por rango de fechas.
- **Validación de Datos**:
  - Validación robusta en los campos de los productos y clientes utilizando `jakarta.validation`.
- **Soft Delete**:
  - Los productos y clientes no se eliminan físicamente de la base de datos.
  - Las búsquedas y listados normales devuelven solo registros activos.
  - Los pedidos históricos se conservan aunque el cliente o producto se den de baja luego.
- **Soporte para CORS**:
  - Configuración habilitada para permitir peticiones desde diferentes orígenes, facilitando la integración con aplicaciones frontend modernas (React, Vue, Angular) que corran en puertos distintos.
- **Manejo Global de Excepciones**:
  - Respuestas de error estandarizadas para `ProductNotFoundException`, `CategoriaNotFoundException`, `ClienteNotFoundException`, `InsufficientStockException`, `IllegalArgumentException`, errores de validación (`MethodArgumentNotValidException`), JSON malformado (`HttpMessageNotReadableException`) y otros errores inesperados.
- **Tecnologías**:
  - Spring Boot
  - Spring Data JPA
  - H2 Database (o cualquier otra base de datos configurada)

## 🗃️ DER del proyecto

El siguiente diagrama resume las entidades principales y sus relaciones en la base de datos.

![DER del proyecto](docs/der.png)

## 📋 Endpoints

A continuación, se detallan los endpoints disponibles en la API y cómo utilizarlos.
La URL base para todos los endpoints es `http://localhost:8080`.

### 1. Obtener todos los productos

- **Método:** `GET`
- **URL:** `/products`
- **Descripción:** Recupera una lista de todos los productos activos existentes en la base de datos.
- **Respuesta Exitosa (200 OK):**
  ```json
  [
    {
      "id": 1,
      "name": "Manzanas",
      "price": 1.5,
      "categoria": {
        "id": 1,
        "nombre": "Frutas",
        "descripcion": "Frutas frescas"
      }
    },
    {
      "id": 2,
      "name": "Leche",
      "price": 2.2,
      "categoria": {
        "id": 2,
        "nombre": "Lácteos",
        "descripcion": "Productos lácteos"
      }
    }
  ]
  ```

### 2. Obtener un producto por ID

- **Método:** `GET`
- **URL:** `/products/{id}`
- **Descripción:** Recupera los detalles de un producto específico utilizando su ID.
- **Respuesta Exitosa (200 OK):**
  ```json
  {
    "id": 1,
    "name": "Manzanas",
    "price": 1.5,
    "categoria": {
      "id": 1,
      "nombre": "Frutas",
      "descripcion": "Frutas frescas"
    }
  }
  ```
- **Respuesta de Error (404 Not Found):**
  ```json
  {
    "message": "Producto con ID 999 no encontrado",
    "timestamp": "2023-10-27T10:00:00.123456",
    "status": 404
  }
  ```

### 3. Crear un nuevo producto

- **Método:** `POST`
- **URL:** `/products`
- **Descripción:** Crea un nuevo producto. Es obligatorio proporcionar el `name`, `price` y el `id` de una `categoria` existente.
- **Cuerpo de la Solicitud (Request Body):**
  ```json
  {
    "name": "Pan Integral",
    "price": 3.0,
    "categoria": {
      "id": 1
    }
  }
  ```
- **Respuesta Exitosa (201 Created):**
  ```json
  {
    "id": 3,
    "name": "Pan Integral",
    "price": 3.0,
    "categoria": {
      "id": 1,
      "nombre": "GENERAL",
      "descripcion": "Categoría por defecto para productos"
    }
  }
  ```
- **Respuesta de Error (400 Bad Request):** (Ejemplos de errores de validación o categoría no encontrada)
  ```json
  {
    "message": "Error de validación en los campos",
    "timestamp": "2023-10-27T10:05:00.123456",
    "status": 400,
    "data": {
      "name": "El nombre debe tener entre 2 y 100 caracteres"
    }
  }
  ```
  O si la categoría no existe:
  ```json
  {
    "message": "Categoría no encontrada con ID: 999",
    "timestamp": "2023-10-27T10:05:00.123456",
    "status": 404
  }
  ```

### 4. Actualizar un producto existente

- **Método:** `PUT`
- **URL:** `/products/{id}`
- **Descripción:** Actualiza parcial o totalmente un producto existente. Solo los campos presentes en el cuerpo de la solicitud y que no sean nulos/vacíos serán actualizados. Para actualizar la categoría, se debe enviar el objeto `categoria` con su `id`.
- **Cuerpo de la Solicitud (Request Body - Ejemplo de actualización de categoría y precio):**
  ```json
  {
    "price": 3.5,
    "categoria": {
      "id": 2
    }
  }
  ```
- **Respuesta Exitosa (200 OK):**
  ```json
  {
    "id": 3,
    "name": "Pan Integral",
    "price": 3.5,
    "categoria": {
      "id": 2,
      "nombre": "Lácteos",
      "descripcion": "Productos lácteos"
    }
  }
  ```
- **Respuesta de Error (404 Not Found / 400 Bad Request):** Similar a los errores en la creación o recuperación.

### 5. Eliminar un producto

- **Método:** `DELETE`
- **URL:** `/products/{id}`
- **Descripción:** Marca un producto como inactivo. El registro permanece en la base de datos.
- **Respuesta Exitosa (200 OK):**
  ```json
  {
    "message": "Producto con ID 3 eliminado exitosamente",
    "timestamp": "2023-10-27T10:15:00.123456",
    "status": 200
  }
  ```
- **Respuesta de Error (404 Not Found):**
  ```json
  {
    "message": "Producto con ID 999 no encontrado",
    "timestamp": "2023-10-27T10:15:00.123456",
    "status": 404
  }
  ```

### 6. Actualizar el stock de un producto

- **Método:** `PATCH`
- **URL:** `/products/{id}/stock`
- **Descripción:** Permite actualizar el stock de un producto utilizando diferentes operaciones (`AGREGAR`, `RESTAR`, `AJUSTAR`). Valida que el stock resultante no sea negativo y lanza una excepción si se intenta restar más cantidad de la disponible.
- **Cuerpo de la Solicitud (Request Body):**
  ```json
  {
    "cantidad": 15,
    "operacion": "AGREGAR"
  }
  ```
- **Respuesta Exitosa (200 OK):**
  ```json
  {
    "id": 1,
    "name": "Manzanas",
    "price": 1.5,
    "stock": 15,
    "categoria": {
      "id": 1,
      "nombre": "Frutas",
      "descripcion": "Frutas frescas"
    }
  }
  ```
- **Respuesta de Error (400 Bad Request - Stock Insuficiente):**
  ```json
  {
    "message": "Stock insuficiente para realizar la operación. Stock actual: 10",
    "timestamp": "2023-10-27T10:20:00.123456",
    "status": 400
  }
  ```

---

### Endpoints de Categorías

Los endpoints para gestionar categorías están bajo el path `/categorias`. Cada producto debe estar asociado a una categoría existente. Al iniciar la aplicación, si no hay categorías en la base de datos, se crea automáticamente la categoría `GENERAL` con ID `1`.

> **Nota:** Al eliminar una categoría, los productos asociados se reasignan automáticamente a `GENERAL`. La categoría con ID `1` no puede eliminarse.

#### 7. Obtener todas las categorías

- **Método:** `GET`
- **URL:** `/categorias`
- **Descripción:** Recupera la lista completa de categorías registradas.
- **Respuesta Exitosa (200 OK):**
  ```json
  [
    {
      "id": 1,
      "nombre": "GENERAL",
      "descripcion": "Categoría por defecto para productos"
    },
    {
      "id": 2,
      "nombre": "Frutas",
      "descripcion": "Frutas frescas"
    }
  ]
  ```

#### 8. Obtener una categoría por ID

- **Método:** `GET`
- **URL:** `/categorias/{id}`
- **Descripción:** Recupera los detalles de una categoría específica utilizando su ID.
- **Respuesta Exitosa (200 OK):**
  ```json
  {
    "id": 2,
    "nombre": "Frutas",
    "descripcion": "Frutas frescas"
  }
  ```
- **Respuesta de Error (404 Not Found):**
  ```json
  {
    "message": "Categoría no encontrada con ID: 999",
    "timestamp": "2026-06-25T10:00:00.123456",
    "status": 404
  }
  ```

#### 9. Crear una nueva categoría

- **Método:** `POST`
- **URL:** `/categorias`
- **Descripción:** Crea una nueva categoría. El campo `nombre` es obligatorio y debe ser único. La `descripcion` es opcional.
- **Cuerpo de la Solicitud (Request Body):**
  ```json
  {
    "nombre": "Lácteos",
    "descripcion": "Productos lácteos"
  }
  ```
- **Respuesta Exitosa (201 Created):**
  ```json
  {
    "id": 3,
    "nombre": "Lácteos",
    "descripcion": "Productos lácteos"
  }
  ```
- **Respuesta de Error (400 Bad Request - nombre vacío):**
  ```json
  {
    "message": "El nombre de la categoría no puede estar vacío",
    "timestamp": "2026-06-25T10:05:00.123456",
    "status": 400
  }
  ```

#### 10. Actualizar una categoría existente

- **Método:** `PUT`
- **URL:** `/categorias/{id}`
- **Descripción:** Actualiza de forma parcial una categoría existente. Solo se actualizan los campos provistos: `nombre` (si no es nulo ni vacío) y `descripcion` (si no es nulo).
- **Cuerpo de la Solicitud (Request Body):**
  ```json
  {
    "nombre": "Lácteos y derivados",
    "descripcion": "Leche, queso, yogur y similares"
  }
  ```
- **Respuesta Exitosa (200 OK):**
  ```json
  {
    "id": 3,
    "nombre": "Lácteos y derivados",
    "descripcion": "Leche, queso, yogur y similares"
  }
  ```
- **Respuesta de Error (404 Not Found):** Similar al endpoint de obtención por ID.

#### 11. Eliminar una categoría

- **Método:** `DELETE`
- **URL:** `/categorias/{id}`
- **Descripción:** Elimina físicamente una categoría. Antes de eliminarla, reasigna todos sus productos a la categoría `GENERAL` (ID `1`). No es posible eliminar la categoría `GENERAL`.
- **Respuesta Exitosa (200 OK):**
  ```json
  {
    "message": "Categoría eliminada correctamente",
    "timestamp": "2026-06-25T10:15:00.123456",
    "status": 200
  }
  ```
- **Respuesta de Error (400 Bad Request - intento de eliminar GENERAL):**
  ```json
  {
    "message": "La categoría 'General' (ID 1) es obligatoria y no puede ser eliminada",
    "timestamp": "2026-06-25T10:15:00.123456",
    "status": 400
  }
  ```
- **Respuesta de Error (404 Not Found):**
  ```json
  {
    "message": "Categoría no encontrada con ID: 999",
    "timestamp": "2026-06-25T10:15:00.123456",
    "status": 404
  }
  ```

---

### Endpoints de Clientes

Los endpoints para gestionar clientes están bajo el path `/clientes`.

#### 12. Obtener todos los clientes

- **Método:** `GET`
- **URL:** `/clientes`
- **Descripción:** Recupera la lista completa de todos los clientes activos.
- **Respuesta Exitosa (200 OK):**
  ```json
  [
    {
      "id": 1,
      "nombre": "Juan",
      "apellido": "Perez",
      "dni": "12345678",
      "email": "juan@example.com",
      "telefono": "11223344",
      "direccion": "Calle Falsa 123"
    }
  ]
  ```

#### 13. Obtener un cliente por ID

- **Método:** `GET`
- **URL:** `/clientes/{id}`
- **Descripción:** Recupera los detalles de un cliente específico utilizando su ID.
- **Respuesta Exitosa (200 OK):**
  ```json
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Perez",
    "dni": "12345678",
    "email": "juan@example.com",
    "telefono": "11223344",
    "direccion": "Calle Falsa 123"
  }
  ```
- **Respuesta de Error (404 Not Found):**
  ```json
  {
    "message": "Cliente con ID 99 no encontrado",
    "timestamp": "2026-06-22T19:00:00.000",
    "status": 404
  }
  ```

#### 14. Obtener un cliente por DNI

- **Método:** `GET`
- **URL:** `/clientes/dni/{dni}`
- **Descripción:** Recupera un cliente específico por su DNI.
- **Respuesta Exitosa (200 OK):** (Igual al formato por ID)

#### 15. Crear un nuevo cliente

- **Método:** `POST`
- **URL:** `/clientes`
- **Descripción:** Crea un nuevo cliente. Los campos `nombre`, `apellido` y `dni` son obligatorios. El `dni` debe ser único en el sistema.
- **Cuerpo de la Solicitud (Request Body):**
  ```json
  {
    "nombre": "María",
    "apellido": "Gómez",
    "dni": "87654321",
    "email": "maria@example.com"
  }
  ```
- **Respuesta Exitosa (201 Created):**
  ```json
  {
    "id": 2,
    "nombre": "María",
    "apellido": "Gómez",
    "dni": "87654321",
    "email": "maria@example.com",
    "telefono": null,
    "direccion": null
  }
  ```
- **Respuesta de Error (400 Bad Request - DNI duplicado o validación fallida):**
  ```json
  {
    "message": "Ya existe un cliente registrado con el DNI 87654321",
    "timestamp": "2026-06-22T19:00:00.000",
    "status": 400
  }
  ```

#### 16. Actualizar un cliente existente

- **Método:** `PUT`
- **URL:** `/clientes/{id}`
- **Descripción:** Actualiza de forma parcial o total un cliente existente. Solo se actualizan los campos provistos no nulos y no vacíos.
- **Cuerpo de la Solicitud (Request Body):**
  ```json
  {
    "nombre": "María Clara",
    "direccion": "Avenida Siempre Viva 742"
  }
  ```
- **Respuesta Exitosa (200 OK):**
  ```json
  {
    "id": 2,
    "nombre": "María Clara",
    "apellido": "Gómez",
    "dni": "87654321",
    "email": "maria@example.com",
    "telefono": null,
    "direccion": "Avenida Siempre Viva 742"
  }
  ```

#### 17. Eliminar un cliente

- **Método:** `DELETE`
- **URL:** `/clientes/{id}`
- **Descripción:** Marca un cliente como inactivo. El registro permanece en la base de datos.
- **Respuesta Exitosa (200 OK):**
  ```json
  {
    "message": "Cliente con ID 2 eliminado exitosamente",
    "timestamp": "2026-06-22T19:00:00.000",
    "status": 200
  }
  ```

### Endpoints de Pedidos

Los pedidos se crean asociados a un cliente y pueden incluir varios productos. El flujo recomendado es crear primero un pedido en estado `BORRADOR`, confirmarlo cuando se quiera descontar stock y cancelarlo si hace falta revertir la operación.

Para probar este flujo desde el navegador, también existe una página de simulación en `src/main/resources/static/pedidos.html`.

#### 18. Crear un pedido

- **Método:** `POST`
- **URL:** `/pedidos`
- **Descripción:** Crea un pedido borrador para un cliente activo.
- **Cuerpo de la Solicitud (Request Body):**
  ```json
  {
    "clienteId": 1,
    "items": [
      { "productId": 2, "cantidad": 3 },
      { "productId": 4, "cantidad": 1 }
    ]
  }
  ```
- **Respuesta Exitosa (201 Created):**
  ```json
  {
    "id": 15,
    "clienteId": 1,
    "clienteNombre": "Juan Perez",
    "estado": "BORRADOR",
    "createdAt": "2026-06-23T10:15:00",
    "updatedAt": null,
    "confirmedAt": null,
    "total": 4500.0,
    "items": [
      {
        "id": 1,
        "productId": 2,
        "productName": "Mouse",
        "cantidad": 3,
        "precioUnitario": 1000.0,
        "subtotal": 3000.0
      }
    ]
  }
  ```

#### 19. Confirmar un pedido

- **Método:** `POST`
- **URL:** `/pedidos/{id}/confirmar`
- **Descripción:** Cambia el pedido a `CONFIRMADO` y descuenta el stock de los productos incluidos.

#### 20. Cancelar un pedido

- **Método:** `POST`
- **URL:** `/pedidos/{id}/cancelar`
- **Descripción:** Cambia el pedido a `CANCELADO`. Si el pedido estaba confirmado, devuelve el stock asociado.

#### 21. Obtener todos los pedidos

- **Método:** `GET`
- **URL:** `/pedidos`
- **Descripción:** Devuelve todos los pedidos cargados.

#### 22. Obtener un pedido por ID

- **Método:** `GET`
- **URL:** `/pedidos/{id}`
- **Descripción:** Recupera el detalle completo de un pedido.

#### 23. Obtener pedidos por cliente

- **Método:** `GET`
- **URL:** `/pedidos/cliente/{clienteId}`
- **Descripción:** Devuelve todos los pedidos asociados a un cliente específico.

#### 24. Obtener pedidos por estado

- **Método:** `GET`
- **URL:** `/pedidos/estado/{estado}`
- **Descripción:** Filtra pedidos por estado. Valores válidos: `BORRADOR`, `CONFIRMADO`, `CANCELADO`.

#### 25. Obtener pedidos por rango de fechas

- **Método:** `GET`
- **URL:** `/pedidos/rango?desde=2026-06-01T00:00:00&hasta=2026-06-30T23:59:59`
- **Descripción:** Devuelve los pedidos creados dentro del rango indicado.

## 🛠️ Cómo Iniciar la Aplicación

1.  **Clonar el repositorio:** (Si aplica)
2.  **Configuración de la Base de Datos:** Asegúrate de que `application.properties` esté configurado correctamente para la base de datos MySql.
3.  **Compilar y Ejecutar:**
   ```bash
   ./mvnw spring-boot:run
   ```
4.  La API estará disponible en `http://localhost:8080`. Se puede probar levantando `index.html` o `pedidos.html` desde `src/main/resources/static` en el navegador. 
  Tambien esta disponible en https://apijava26.mabcontrol.ar
