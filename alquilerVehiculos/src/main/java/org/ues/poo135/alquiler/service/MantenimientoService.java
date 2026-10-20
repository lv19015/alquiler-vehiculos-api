package org.ues.poo135.alquiler.service;

import org.ues.poo135.alquiler.dto.CrearMantenimientoDTO;
import org.ues.poo135.alquiler.dto.MantenimientoResponseDTO;

import java.util.List;

public interface MantenimientoService {
    MantenimientoResponseDTO registrar(CrearMantenimientoDTO dto);
    List<MantenimientoResponseDTO> listarTodos();
    MantenimientoResponseDTO buscarPorId(Long id);
    MantenimientoResponseDTO actualizar(Long id, CrearMantenimientoDTO dto);
    void eliminar(Long id);
}