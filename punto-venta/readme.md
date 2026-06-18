# API de Punto de Venta

Esta es una API RESTful para gestionar productos en un sistema de punto de venta. Permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre productos, incluyendo la gestión de categorías asociadas.

## 🚀 Características

- **Gestión de Productos**:
  - Creación de nuevos productos con nombre, precio y categoría.
  - Consulta de todos los productos o de un producto específico por su ID.
  - Actualización parcial de productos (nombre, precio, categoría).
  - Eliminación de productos.
- **Gestión de Categorías**:
  - Cada producto está asociado a una categoría existente.
  - Verificación automática de categorías: Si la base de datos de categorías está vacía al iniciar la aplicación, se crea una categoría `GENERAL` por defecto con ID `1`.
- **Validación de Datos**:
  - Validación robusta en los campos de los productos (nombre, precio, categoría) utilizando `jakarta.validation`.
- **Manejo Global de Excepciones**:
  - Respuestas de error estandarizadas para `ProductNotFoundException`, `CategoriaNotFoundException`, `IllegalArgumentException`, errores de validación (`MethodArgumentNotValidException`), JSON malformado (`HttpMessageNotReadableException`) y otros errores inesperados.
- **Tecnologías**:
  - Spring Boot
  - Spring Data JPA
  - H2 Database (o cualquier otra base de datos configurada)
  - Lombok (asumiendo su uso para getters/setters)

## 📋 Endpoints

A continuación, se detallan los endpoints disponibles en la API y cómo utilizarlos.
La URL base para todos los endpoints es `http://localhost:8080`.

### 1. Obtener todos los productos

- **Método:** `GET`
- **URL:** `/products`
- **Descripción:** Recupera una lista de todos los productos existentes en la base de datos.
- **Respuesta Exitosa (200 OK):**
  ```json
  [
    {
      "id": 1,
      "name": "Manzanas",
      "price": 1.5,
      "categoria": {
        "id": 1,
        "name": "Frutas",
        "description": "Frutas frescas"
      }
    },
    {
      "id": 2,
      "name": "Leche",
      "price": 2.2,
      "categoria": {
        "id": 2,
        "name": "Lácteos",
        "description": "Productos lácteos"
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
      "name": "Frutas",
      "description": "Frutas frescas"
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
      "name": "GENERAL",
      "description": "Categoría por defecto para productos"
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
    "message": "Categoría con ID 999 no encontrada",
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
      "name": "Lácteos",
      "description": "Productos lácteos"
    }
  }
  ```
- **Respuesta de Error (404 Not Found / 400 Bad Request):** Similar a los errores en la creación o recuperación.

### 5. Eliminar un producto

- **Método:** `DELETE`
- **URL:** `/products/{id}`
- **Descripción:** Elimina un producto específico por su ID.
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

## 🛠️ Cómo Iniciar la Aplicación

1.  **Clonar el repositorio:** (Si aplica)
2.  **Configuración de la Base de Datos:** 
    Asegúrate de que `src/main/resources/application.properties` esté configurado correctamente para tu instancia de MySQL. 
    
    Para que Hibernate cree las tablas automáticamente, asegúrate de incluir:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/nombre_tu_bd
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    ```

3.  **Compilar y Ejecutar:**
   ```bash
   ./mvnw spring-boot:run
   ```
4.  La API estará disponible en `http://localhost:8080`.