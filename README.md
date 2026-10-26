# API - Sistema de Alquiler de Vehículos

**Asignatura:** Programación Orientada a Objetos (POO135) — Universidad de El Salvador  
**Ciclo:** II - 2026  

## Integrantes del Equipo - Grupo 10

| Nombre Completo | Correo / Carnet |
| :--- | :--- |
| Catherine Andrea Argumedo Barahona | AB25013@ues.edu.sv |
| Franklin Omar García Román | GR20019@ues.edu.sv |
| José Edenilson Guardado López | GL25010@ues.edu.sv |
| Paola Sugey Hércules Jirón | HJ23002@ues.edu.sv |
| Brenda Ivania Laínez Vides | LV19015@ues.edu.sv |

---

## Descripción del Proyecto

API REST desarrollada con Spring Boot para la gestión y automatización del alquiler de vehículos. El sistema permite registrar clientes, administrar el inventario de vehículos y gestionar el ciclo de vida de las reservas validando la disponibilidad del vehículo en un rango de fechas determinado.

### Lógica de Negocio Principal

* **Gestión de Estados:** Los vehículos cambian segun estados `Disponible`, `Alquilado` y `En Mantenimiento`.
* **Validación de Reservas:** Para confirmar una reserva, la API valida que el vehículo esté en estado `Disponible` y no posea traslapes de fechas con reservas previas.
* **Operaciones HTTP:** Implementación de estándares REST mediante verbos `GET`, `POST`, `PUT` y `DELETE`.

---

## Tecnologías y Herramientas

* **Lenguaje:** Java
* **Gestor de Construcción:** Gradle / Maven
* **Modelado UML:** PlantUML / Lucidchart
* **Control de Versiones:** Git & GitHub

---

## Documentación de Diseño (Entrega #1)

#### 1. Diagrama Entidad-Relación (DER)
#### 2. Diagrama de Clases UML
#### 3. Diagrama de Casos de Uso

---
## Tecnologías Utilizadas (Entrega #2)
* **Java 21**
* **Spring Boot 3.5.5** 
* **Gradle** 
* **H2 Database** 
* **Lombok**
* **Postman** 
---
## Endpoints Implementados
### Vehículos (`/api/vehiculos`)
* `POST /api/vehiculos` - Registrar un vehículo nuevo.
* `GET /api/vehiculos` - Listar todos los vehículos.
* `GET /api/vehiculos/{id}` - Obtener vehículo por ID.
* `GET /api/vehiculos/disponibles` - Listar vehículos disponibles.
* `GET /api/vehiculos/{id}/disponibilidad` - Consultar disponibilidad por fechas.
* `PUT /api/vehiculos/{id}` - Actualizar datos del vehículo.
* `PATCH /api/vehiculos/{id}/estado` - Modificar estado del vehículo.
* `DELETE /api/vehiculos/{id}` - Eliminar un vehículo.
### Mantenimientos (`/api/mantenimientos`)
* `POST /api/mantenimientos` - Registrar mantenimiento (cambia estado del vehículo a `EN_MANTENIMIENTO`).
* `GET /api/mantenimientos` - Listar todos los mantenimientos.
* `GET /api/mantenimientos/{id}` - Consultar mantenimiento por ID.
* `PUT /api/mantenimientos/{id}` - Actualizar registro de mantenimiento.
* `DELETE /api/mantenimientos/{id}` - Eliminar registro de mantenimiento.

### Clientes (`/api/clientes`)
* `POST /api/clientes` - Registrar un cliente nuevo.
* `GET /api/clientes` - Listar todos los clientes.
* `GET /api/clientes/{id}` - Obtener cliente por ID.
* `GET /api/clientes/documento/{documentoIdentidad}` - Buscar cliente por documento de identidad.
* `PUT /api/clientes/{id}` - Actualizar datos del cliente.
* `DELETE /api/clientes/{id}` - Eliminar un cliente.

**Campos del cliente:** `nombre`, `apellido`, `documentoIdentidad` (único), `correo` (único), `telefono` y `licenciaConducir`.

**Validaciones:** todos los campos son obligatorios; el correo debe tener formato válido; el teléfono debe tener 8 dígitos (ejemplo: `7777-1234`); el documento de identidad debe tener entre 5 y 20 caracteres alfanuméricos o guiones.

**Respuestas:** `201` al crear, `200` al consultar o actualizar, `204` al eliminar, `400` por datos inválidos, `404` si el cliente no existe y `409` si el documento o el correo ya están registrados.

### Reservas *(En desarrollo)*

## Manejo de Excepciones

La API cuenta con un manejador global de errores (`GlobalExceptionHandler`) que devuelve respuestas unificadas en formato JSON ante errores de validación (400), recursos no encontrados (404), conflictos de unicidad (409) o errores internos (500).

---
## Base de Datos y Datos Iniciales
Se utiliza una base de datos **H2 en memoria** precargada con datos iniciales mediante el script `data.sql`.
* **Consola H2:** `http://localhost:8080/h2-console`
* **JDBC URL:** `jdbc:h2:mem:alquilerdb`
* **Usuario:** `sa`
* **Contraseña:** *(vacía)*
---

## Pruebas con Postman
En la raíz del proyecto se incluye el archivo:
> **`AlquilerVehiculos_PostmanCollection.json`**