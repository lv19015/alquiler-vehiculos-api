package org.ues.poo135.alquiler.service;

import org.ues.poo135.alquiler.dto.CrearVehiculoDTO;
import org.ues.poo135.alquiler.dto.VehiculoResponseDTO;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;

import java.time.LocalDate;
import java.util.List;

public interface VehiculoService {

    VehiculoResponseDTO crearVehiculo(CrearVehiculoDTO dto);

    List<VehiculoResponseDTO> obtenerTodos();

    VehiculoResponseDTO obtenerPorId(Long id);

    VehiculoResponseDTO actualizarVehiculo(Long id, CrearVehiculoDTO dto);

    VehiculoResponseDTO cambiarEstado(Long id, EstadoVehiculo nuevoEstado);

    void eliminarVehiculo(Long id);

    List<VehiculoResponseDTO> obtenerDisponibles();

    boolean verificarDisponibilidad(Long vehiculoId, LocalDate fechaInicio, LocalDate fechaFin);
}
