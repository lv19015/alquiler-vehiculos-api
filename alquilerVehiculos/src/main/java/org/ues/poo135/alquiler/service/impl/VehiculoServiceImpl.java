package org.ues.poo135.alquiler.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ues.poo135.alquiler.dto.CrearVehiculoDTO;
import org.ues.poo135.alquiler.dto.VehiculoResponseDTO;
import org.ues.poo135.alquiler.exception.ConflictException;
import org.ues.poo135.alquiler.exception.ResourceNotFoundException;
import org.ues.poo135.alquiler.model.Reserva.EstadoReserva;
import org.ues.poo135.alquiler.model.Vehiculo;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;
import org.ues.poo135.alquiler.repository.ReservaRepository;
import org.ues.poo135.alquiler.repository.VehiculoRepository;
import org.ues.poo135.alquiler.rules.ReglasEstadoVehiculo;
import org.ues.poo135.alquiler.service.DisponibilidadService;
import org.ues.poo135.alquiler.service.VehiculoService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ReservaRepository reservaRepository;
    private final DisponibilidadService disponibilidadService;

    @Override
    public VehiculoResponseDTO crearVehiculo(CrearVehiculoDTO dto) {
        if (vehiculoRepository.existsByPlaca(dto.getPlaca())) {
            throw new ConflictException(
                "Ya existe un vehiculo registrado con la placa: " + dto.getPlaca());
        }

        Vehiculo vehiculo = Vehiculo.builder()
            .marca(dto.getMarca())
            .modelo(dto.getModelo())
            .placa(dto.getPlaca())
            .categoria(dto.getCategoria())
            .precioPorDia(dto.getPrecioPorDia())
            .estado(EstadoVehiculo.DISPONIBLE)
            .build();

        return toResponseDTO(vehiculoRepository.save(vehiculo));
    }

    @Override
    public List<VehiculoResponseDTO> obtenerTodos() {
        return vehiculoRepository.findAll()
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    @Override
    public VehiculoResponseDTO obtenerPorId(Long id) {
        return toResponseDTO(buscarPorIdOLanzarError(id));
    }

    @Override
    public VehiculoResponseDTO actualizarVehiculo(Long id, CrearVehiculoDTO dto) {
        Vehiculo vehiculo = buscarPorIdOLanzarError(id);

        // Si cambio la placa, verificar que la nueva no este en uso por otro vehiculo
        if (!vehiculo.getPlaca().equals(dto.getPlaca())
                && vehiculoRepository.existsByPlaca(dto.getPlaca())) {
            throw new ConflictException(
                "La placa " + dto.getPlaca() + " ya esta en uso por otro vehiculo.");
        }

        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setPlaca(dto.getPlaca());
        vehiculo.setCategoria(dto.getCategoria());
        vehiculo.setPrecioPorDia(dto.getPrecioPorDia());

        return toResponseDTO(vehiculoRepository.save(vehiculo));
    }

    @Override
    public VehiculoResponseDTO cambiarEstado(Long id, EstadoVehiculo nuevoEstado) {
        Vehiculo vehiculo = buscarPorIdOLanzarError(id);

        // Regla 1: la transicion debe existir en la maquina de estados
        ReglasEstadoVehiculo.validarTransicion(vehiculo.getEstado(), nuevoEstado);

        // Regla 2: no se libera un vehiculo con una reserva activa
        if (nuevoEstado == EstadoVehiculo.DISPONIBLE
                && reservaRepository.existsByVehiculoIdAndEstado(id, EstadoReserva.ACTIVA)) {
            throw new ConflictException(
                "El vehiculo tiene una reserva activa. Cancele o finalice la reserva primero.");
        }

        vehiculo.setEstado(nuevoEstado);
        return toResponseDTO(vehiculoRepository.save(vehiculo));
    }

    @Override
    public void eliminarVehiculo(Long id) {
        Vehiculo vehiculo = buscarPorIdOLanzarError(id);

        if (vehiculo.getEstado() == EstadoVehiculo.ALQUILADO) {
            throw new ConflictException(
                "No se puede eliminar un vehiculo que esta actualmente alquilado.");
        }
        if (reservaRepository.existsByVehiculoId(id)) {
            throw new ConflictException(
                "No se puede eliminar un vehiculo que tiene reservas registradas.");
        }

        vehiculoRepository.delete(vehiculo);
    }

    @Override
    public List<VehiculoResponseDTO> obtenerDisponibles() {
        return vehiculoRepository.findByEstado(EstadoVehiculo.DISPONIBLE)
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    @Override
    public boolean verificarDisponibilidad(Long vehiculoId, LocalDate fechaInicio, LocalDate fechaFin) {
        Vehiculo vehiculo = buscarPorIdOLanzarError(vehiculoId);
        disponibilidadService.validarRangoFechas(fechaInicio, fechaFin);
        return disponibilidadService.estaDisponible(vehiculo, fechaInicio, fechaFin);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private Vehiculo buscarPorIdOLanzarError(Long id) {
        return vehiculoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Vehiculo con ID " + id + " no encontrado."));
    }

    private VehiculoResponseDTO toResponseDTO(Vehiculo v) {
        return VehiculoResponseDTO.builder()
            .id(v.getId())
            .marca(v.getMarca())
            .modelo(v.getModelo())
            .placa(v.getPlaca())
            .categoria(v.getCategoria())
            .precioPorDia(v.getPrecioPorDia())
            .estado(v.getEstado().name())
            .build();
    }
}
