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

API REST desarrollada con Spring Boot y Java 21 para la gestión integral y automatización del alquiler de vehículos. El sistema administra el inventario de la flota, el registro y control de clientes, la programación de mantenimientos preventivos y correctivos, y el ciclo de vida completo de las reservas y contratos de alquiler.

### Lógica de Negocio Principal

* **Gestión de Estados de Flota:** Los vehículos transicionan dinámicamente entre `DISPONIBLE`, `ALQUILADO` y `EN_MANTENIMIENTO`.
* **Control de Disponibilidad y Fechas:** Validación estricta que impide traslapes de fechas en reservas o alquileres para un mismo vehículo.
* **Integración Mantenimiento-Vehículo:** Al registrar un mantenimiento, el vehículo asociado cambia automáticamente su estado a `EN_MANTENIMIENTO`.
* **Manejo Centralizado de Errores:** Respuestas estandarizadas en formato JSON mediante `@RestControllerAdvice` ante errores de cliente (400), no encontrados (404), conflictos de integridad o fechas (409) y errores internos (500).

---

## Tecnologías y Herramientas

* **Lenguaje:** Java 21 (Temurin)
* **Framework:** Spring Boot 3.5.5 (Spring Web, Spring Data JPA, Bean Validation)
* **Gestor de Construcción:** Gradle 8.9 (Kotlin DSL)
* **Base de Datos:** H2 Database (en memoria)
* **Utilidades:** Project Lombok
* **Documentación Interactiva:** Swagger UI & Springdoc OpenAPI
* **Testing:** JUnit 5 & Mockito
* **Contenerización y Orquestación:** Docker & Kubernetes (Docker Desktop)
* **CI/CD Local:** GitHub Actions simulado con `act`
* **Control de Versiones:** Git & GitHub

---

## Documentación de Diseño (Entrega #1)

1. Diagrama Entidad-Relación (DER)
2. Diagrama de Clases UML
3. Casos de Uso

---

## Endpoints Implementados

### 1. Vehículos (`/api/vehiculos`)
* `POST /api/vehiculos` - Registrar un vehículo nuevo (valida placa única).
* `GET /api/vehiculos` - Listar todos los vehículos.
* `GET /api/vehiculos/{id}` - Obtener vehículo por ID.
* `GET /api/vehiculos/disponibles` - Listar vehículos disponibles.
* `GET /api/vehiculos/{id}/disponibilidad` - Consultar disponibilidad por rango de fechas.
* `PUT /api/vehiculos/{id}` - Actualizar datos del vehículo.
* `PATCH /api/vehiculos/{id}/estado` - Modificar estado del vehículo (`DISPONIBLE`, `ALQUILADO`, `EN_MANTENIMIENTO`).
* `DELETE /api/vehiculos/{id}` - Eliminar un vehículo (restringido si está alquilado o posee mantenimientos asociados).

### 2. Mantenimientos (`/api/mantenimientos`)
* `POST /api/mantenimientos` - Registrar mantenimiento (cambia automáticamente el vehículo a `EN_MANTENIMIENTO`).
* `GET /api/mantenimientos` - Listar todos los mantenimientos.
* `GET /api/mantenimientos/{id}` - Consultar mantenimiento por ID.
* `PUT /api/mantenimientos/{id}` - Actualizar registro de mantenimiento.
* `DELETE /api/mantenimientos/{id}` - Eliminar registro de mantenimiento.

### 3. Clientes (`/api/clientes`)
* `POST /api/clientes` - Registrar un nuevo cliente (valida unicidad de documento y correo).
* `GET /api/clientes` - Listar todos los clientes.
* `GET /api/clientes/{id}` - Obtener cliente por ID.
* `GET /api/clientes/documento/{documentoIdentidad}` - Buscar cliente por documento.
* `PUT /api/clientes/{id}` - Actualizar datos del cliente.
* `DELETE /api/clientes/{id}` - Eliminar un cliente.

### 4. Reservas (`/api/reservas`)
* `POST /api/reservas` - Crear una nueva reserva (valida disponibilidad de fechas y estado del vehículo).
* `GET /api/reservas` - Listar todas las reservas.
* `GET /api/reservas/{id}` - Obtener reserva por ID.
* `GET /api/reservas/cliente/{clienteId}` - Listar reservas de un cliente.
* `PUT /api/reservas/{id}` - Modificar rango de fechas de una reserva activa.
* `PATCH /api/reservas/{id}/cancelar` - Cancelar reserva y liberar el vehículo a `DISPONIBLE`.
* `PATCH /api/reservas/{id}/finalizar` - Finalizar reserva y liberar el vehículo.
* `DELETE /api/reservas/{id}` - Eliminar una reserva.

### 5. Alquileres (`/api/alquileres`)
* `POST /api/alquileres` - Crear contrato de alquiler activo (cambia vehículo a `ALQUILADO`).
* `GET /api/alquileres` - Listar todos los alquileres.
* `GET /api/alquileres/{id}` - Obtener alquiler por ID.
* `GET /api/alquileres/cliente/{clienteId}` - Listar alquileres de un cliente.
* `GET /api/alquileres/vehiculo/{vehiculoId}` - Listar alquileres de un vehículo.
* `PUT /api/alquileres/{id}/finalizar` - Finalizar alquiler y liberar el vehículo.
* `PUT /api/alquileres/{id}/cancelar` - Cancelar alquiler y liberar el vehículo.
* `DELETE /api/alquileres/{id}` - Eliminar registro de alquiler.

---

## Documentación Interactiva (Swagger / OpenAPI)

La API cuenta con documentación interactiva autogenerada con OpenAPI 3:

* **URL Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
* **Especificación OpenAPI (JSON):** `http://localhost:8080/v3/api-docs`

---

## Base de Datos y Datos Iniciales

Se utiliza **H2 Database en memoria** precargada con datos de prueba iniciales desde `src/main/resources/data.sql`:

* **Consola Web H2:** `http://localhost:8080/h2-console`
* **JDBC URL:** `jdbc:h2:mem:alquilerdb`
* **Usuario:** `sa`
* **Contraseña:** *(vacía)*

---

## Pruebas Unitarias

La calidad del código se encuentra respaldada por pruebas unitarias usando **JUnit 5** y **Mockito**:

```bash
cd alquilerVehiculos
./gradlew test
```

---

## Instrucciones de Despliegue y DevOps

### 1. Ejecución Local con Gradle
```bash
cd alquilerVehiculos
./gradlew bootRun
```

### 2. Contenerización con Docker
El proyecto cuenta con un `Dockerfile` multietapa optimizado para generar una imagen ligera:

```bash
# Construir la imagen Docker
docker build -t alquiler-vehiculos:latest ./alquilerVehiculos

# Ejecutar el contenedor
docker run -d -p 8080:8080 --name api-alquiler alquiler-vehiculos:latest
```

### 3. Orquestación en Kubernetes Local (Docker Desktop)
Los manifiestos se encuentran organizados en la carpeta `k8s/`:

```bash
# Aplicar el Deployment y el Service
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml

# Verificar el estado del despliegue y pods
kubectl get pods -l app=alquiler-vehiculos
kubectl get service alquiler-vehiculos-service
```
El servicio estará accesible en `http://localhost:8080`.

### 4. Automatización CI/CD Local con `act`
El flujo de CI/CD automatiza las fases de **Build, Test y Deploy** simulando GitHub Actions en local:

```bash
# Ejecutar el pipeline localmente con act
act push
```
La conexión segura al clúster local de Kubernetes se gestiona a través del archivo `.kube-local/config`.

---

## Pruebas con Postman

En la raíz del proyecto se incluye el archivo:
> **`AlquilerVehiculos_PostmanCollection.json`**

Incluye todas las solicitudes organizadas por carpetas para probar los módulos (`Vehículos`, `Mantenimientos`, `Clientes`, `Reservas`, `Alquileres`), abarcando respuestas exitosas (200, 201, 204) y respuestas de error controladas (400, 404, 409), con ejemplos de respuesta guardados para cada caso.