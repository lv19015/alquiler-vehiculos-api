-- Inserción de vehículos iniciales de prueba
INSERT INTO vehiculos (marca, modelo, placa, categoria, precio_por_dia, estado)
VALUES ('Toyota', 'Corolla', 'P123456', 'Sedan', 35.0, 'DISPONIBLE');

INSERT INTO vehiculos (marca, modelo, placa, categoria, precio_por_dia, estado)
VALUES ('Nissan', 'Frontier', 'P987654', 'Pickup', 55.0, 'DISPONIBLE');

INSERT INTO vehiculos (marca, modelo, placa, categoria, precio_por_dia, estado)
VALUES ('Hyundai', 'Tucson', 'P456789', 'SUV', 45.0, 'EN_MANTENIMIENTO');

INSERT INTO vehiculos (marca, modelo, placa, categoria, precio_por_dia, estado)
VALUES ('Kia', 'Rio', 'P321654', 'Sedan', 30.0, 'ALQUILADO');

INSERT INTO vehiculos (marca, modelo, placa, categoria, precio_por_dia, estado)
VALUES ('Honda', 'Civic', 'P654321', 'Sedan', 45.00, 'DISPONIBLE');

-- Inserción de un registro inicial de mantenimiento para el vehículo 3
INSERT INTO mantenimientos (vehiculo_id, fecha_inicio, fecha_fin, descripcion, costo)
VALUES (3, '2026-10-20', '2026-10-24', 'Revisión periódica y cambio de aceite', 85.00);

-- Inserción de un registro de mantenimiento/reparación para el vehículo 5
INSERT INTO mantenimientos (vehiculo_id, fecha_inicio, fecha_fin, descripcion, costo)
VALUES (5, '2026-11-05', '2026-11-06', 'Cambio de pastillas de freno y rectificación de discos', 150.00);