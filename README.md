# API - Sistema de Alquiler de Vehículos

**Asignatura:** Programación Orientada a Objetos (POO135) — Universidad de El Salvador  
**Ciclo:** II - 2026  

---

## Descripción del Proyecto

API REST desarrollada para la gestión y automatización del alquiler de vehículos. El sistema permite registrar clientes, administrar el inventario de vehículos y gestionar el ciclo de vida de las reservas validando la disponibilidad del vehículo en un rango de fechas determinado.

### Lógica de Negocio Principal

* **Gestión de Estados:** Los vehículos cambian segun estados `Disponible`, `Alquilado` y `En Mantenimiento`.
* **Validación de Reservas:** Para confirmar una reserva, la API valida que el vehículo esté en estado `Disponible` y no posea traslapes de fechas con reservas previas.
* **Operaciones HTTP:** Implementación de estándares REST mediante verbos `GET`, `POST`, `PUT` y `DELETE`.

---

## Integrantes del Equipo - Grupo 10

| Nombre Completo | Correo / Carnet |
| :--- | :--- |
| Catherine Andrea Argumedo Barahona | AB25013@ues.edu.sv |
| Franklin Omar García Román | GR20019@ues.edu.sv |
| José Edenilson Guardado López | gl25010@ues.edu.sv |
| Paola Sugey Hércules Jirón | HJ23002@ues.edu.sv |
| Brenda Ivania Laínez Vides | LV19015@ues.edu.sv |

---

## Tecnologías y Herramientas

* **Lenguaje:** Java
* **Gestor de Construcción:** Gradle / Maven
* **Modelado UML:** PlantUML / Lucidchart
* **Control de Versiones:** Git & GitHub

---

## Documentación de Diseño (Entrega #1)

### 1. Diagrama de Casos de Uso

### 2. Diagrama Entidad-Relación (DER)

### 3. Diagrama de Clases UML

---

## Especificación Inicial de Endpoints REST

| Módulo | Método HTTP | Ruta / Endpoint | Descripción |
| :--- | :---: | :--- | :--- |
| **Vehículos** | `POST` | `/api/vehiculos` | Registrar un nuevo vehículo |
| | `GET` | `/api/vehiculos` | Consultar inventario y filtrar por disponibilidad |
| | `PUT` | `/api/vehiculos/{id}` | Actualizar datos o cambiar estado del vehículo |
| | `DELETE` | `/api/vehiculos/{id}` | Eliminar registro de vehículo |
| **Clientes** | `POST` | `/api/clientes` | Registrar un nuevo cliente |
| | `GET` | `/api/clientes/{id}` | Consultar datos del cliente |
| **Reservas** | `POST` | `/api/reservas` | Crear reserva (Valida fechas y disponibilidad) |
| | `GET` | `/api/reservas` | Consultar historial de reservas |
| | `DELETE` | `/api/reservas/{id}` | Cancelar una reserva existente |
