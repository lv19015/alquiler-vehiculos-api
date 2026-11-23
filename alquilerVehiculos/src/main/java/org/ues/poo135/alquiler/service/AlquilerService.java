package org.ues.poo135.alquiler.service;

import org.ues.poo135.alquiler.dto.AlquilerResponseDTO;
import org.ues.poo135.alquiler.dto.CrearAlquilerDTO;

import java.util.List;

public interface AlquilerService {
    List<AlquilerResponseDTO> listarTodos();
    AlquilerResponseDTO obtenerPorId(Long id);
    List<AlquilerResponseDTO> listarPorCliente(Long clienteId);
    List<AlquilerResponseDTO> listarPorVehiculo(Long vehiculoId);
    AlquilerResponseDTO crear(CrearAlquilerDTO dto);
    AlquilerResponseDTO finalizar(Long id);
    AlquilerResponseDTO cancelar(Long id);
    void eliminar(Long id);
}