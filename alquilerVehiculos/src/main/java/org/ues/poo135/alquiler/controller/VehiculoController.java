package org.ues.poo135.alquiler.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ues.poo135.alquiler.dto.CrearVehiculoDTO;
import org.ues.poo135.alquiler.dto.VehiculoResponseDTO;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;
import org.ues.poo135.alquiler.service.VehiculoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    // POST /api/vehiculos
    @PostMapping
    public ResponseEntity<VehiculoResponseDTO> crear(@Valid @RequestBody CrearVehiculoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.crearVehiculo(dto));
    }

    // GET /api/vehiculos
    @GetMapping
    public ResponseEntity<List<VehiculoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(vehiculoService.obtenerTodos());
    }

    // GET /api/vehiculos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculoService.obtenerPorId(id));
    }

    // GET /api/vehiculos/disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<VehiculoResponseDTO>> obtenerDisponibles() {
        return ResponseEntity.ok(vehiculoService.obtenerDisponibles());
    }

    // GET /api/vehiculos/{id}/disponibilidad?fechaInicio=2025-07-10&fechaFin=2025-07-15
    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<Map<String, Object>> verificarDisponibilidad(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        boolean disponible = vehiculoService.verificarDisponibilidad(id, fechaInicio, fechaFin);
        return ResponseEntity.ok(Map.of(
            "vehiculoId", id,
            "fechaInicio", fechaInicio.toString(),
            "fechaFin", fechaFin.toString(),
            "disponible", disponible
        ));
    }

    // PUT /api/vehiculos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CrearVehiculoDTO dto) {
        return ResponseEntity.ok(vehiculoService.actualizarVehiculo(id, dto));
    }

    // PATCH /api/vehiculos/{id}/estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<VehiculoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoVehiculo nuevoEstado) {
        return ResponseEntity.ok(vehiculoService.cambiarEstado(id, nuevoEstado));
    }

    // DELETE /api/vehiculos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vehiculoService.eliminarVehiculo(id);
        return ResponseEntity.noContent().build();
    }
}
