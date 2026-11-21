package org.ues.poo135.alquiler.service;

import org.ues.poo135.alquiler.dto.ActualizarReservaDTO;
import org.ues.poo135.alquiler.dto.CrearReservaDTO;
import org.ues.poo135.alquiler.dto.ReservaResponseDTO;

import java.util.List;

public interface ReservaService {

    ReservaResponseDTO crearReserva(CrearReservaDTO dto);

    List<ReservaResponseDTO> obtenerTodas();

    ReservaResponseDTO obtenerPorId(Long id);

    List<ReservaResponseDTO> obtenerPorCliente(Long clienteId);

    ReservaResponseDTO actualizarReserva(Long id, ActualizarReservaDTO dto);

    ReservaResponseDTO cancelarReserva(Long id);

    ReservaResponseDTO finalizarReserva(Long id);

    void eliminarReserva(Long id);
}
