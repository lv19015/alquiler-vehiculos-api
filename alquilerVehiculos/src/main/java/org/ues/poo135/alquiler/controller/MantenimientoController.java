package org.ues.poo135.alquiler.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ues.poo135.alquiler.dto.CrearMantenimientoDTO;
import org.ues.poo135.alquiler.dto.MantenimientoResponseDTO;
import org.ues.poo135.alquiler.service.MantenimientoService;

import java.util.List;

@RestController
@RequestMapping("/api/mantenimientos")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    @PostMapping
    public ResponseEntity<MantenimientoResponseDTO> registrar(@Valid @RequestBody CrearMantenimientoDTO dto) {
        return new ResponseEntity<>(mantenimientoService.registrar(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MantenimientoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(mantenimientoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MantenimientoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MantenimientoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CrearMantenimientoDTO dto) {
        return ResponseEntity.ok(mantenimientoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mantenimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}