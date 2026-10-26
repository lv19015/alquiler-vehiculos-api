package org.ues.poo135.alquiler.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ues.poo135.alquiler.dto.ClienteResponseDTO;
import org.ues.poo135.alquiler.dto.CrearClienteDTO;
import org.ues.poo135.alquiler.service.ClienteService;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // POST /api/clientes
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crear(@Valid @RequestBody CrearClienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crearCliente(dto));
    }

    // GET /api/clientes
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(clienteService.obtenerTodos());
    }

    // GET /api/clientes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    // GET /api/clientes/documento/{documentoIdentidad}
    @GetMapping("/documento/{documentoIdentidad}")
    public ResponseEntity<ClienteResponseDTO> obtenerPorDocumento(@PathVariable String documentoIdentidad) {
        return ResponseEntity.ok(clienteService.obtenerPorDocumento(documentoIdentidad));
    }

    // PUT /api/clientes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CrearClienteDTO dto) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, dto));
    }

    // DELETE /api/clientes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}