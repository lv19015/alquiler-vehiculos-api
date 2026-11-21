package org.ues.poo135.alquiler.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ues.poo135.alquiler.dto.ActualizarReservaDTO;
import org.ues.poo135.alquiler.dto.CrearReservaDTO;
import org.ues.poo135.alquiler.dto.ReservaResponseDTO;
import org.ues.poo135.alquiler.service.ReservaService;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    // POST /api/reservas
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody CrearReservaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crearReserva(dto));
    }

    // GET /api/reservas
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(reservaService.obtenerTodas());
    }

    // GET /api/reservas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    // GET /api/reservas/cliente/{clienteId}
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.obtenerPorCliente(clienteId));
    }

    // PUT /api/reservas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarReservaDTO dto) {
        return ResponseEntity.ok(reservaService.actualizarReserva(id, dto));
    }

    // PATCH /api/reservas/{id}/cancelar
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelarReserva(id));
    }

    // PATCH /api/reservas/{id}/finalizar
    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<ReservaResponseDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.finalizarReserva(id));
    }

    // DELETE /api/reservas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.noContent().build();
    }
}
