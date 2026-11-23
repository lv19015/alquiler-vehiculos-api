package org.ues.poo135.alquiler.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.ues.poo135.alquiler.dto.AlquilerResponseDTO;
import org.ues.poo135.alquiler.dto.CrearAlquilerDTO;
import org.ues.poo135.alquiler.service.AlquilerService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/alquileres")
@RequiredArgsConstructor
public class AlquilerController {

    private final AlquilerService alquilerService;

    @GetMapping
    public ResponseEntity<List<AlquilerResponseDTO>> listarTodos() {
        return ResponseEntity.ok(alquilerService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlquilerResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<AlquilerResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(alquilerService.listarPorCliente(clienteId));
    }

    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<List<AlquilerResponseDTO>> listarPorVehiculo(@PathVariable Long vehiculoId) {
        return ResponseEntity.ok(alquilerService.listarPorVehiculo(vehiculoId));
    }

    @PostMapping
    public ResponseEntity<AlquilerResponseDTO> crear(@Valid @RequestBody CrearAlquilerDTO dto) {
        AlquilerResponseDTO creado = alquilerService.crear(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<AlquilerResponseDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.finalizar(id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<AlquilerResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.cancelar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alquilerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}