package org.ues.poo135.alquiler.service.impl;

import lombok.RequiredArgsConstructor;
import org.ues.poo135.alquiler.dto.CrearVehiculoDTO;
import org.ues.poo135.alquiler.dto.VehiculoResponseDTO;
import org.ues.poo135.alquiler.model.Vehiculo;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;
import org.ues.poo135.alquiler.repository.VehiculoRepository;
import org.ues.poo135.alquiler.service.VehiculoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    @Override
    public VehiculoResponseDTO crearVehiculo(CrearVehiculoDTO dto) {
        if (vehiculoRepository.existsByPlaca(dto.getPlaca())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe un vehiculo registrado con la placa: " + dto.getPlaca()
            );
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
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "La placa " + dto.getPlaca() + " ya esta en uso por otro vehiculo."
            );
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

        // No se puede marcar como DISPONIBLE un vehiculo que tiene reservas activas
        if (nuevoEstado == EstadoVehiculo.DISPONIBLE
                && vehiculo.getEstado() == EstadoVehiculo.ALQUILADO) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No se puede marcar como DISPONIBLE un vehiculo con reserva activa. Finalize la reserva primero."
            );
        }

        vehiculo.setEstado(nuevoEstado);
        return toResponseDTO(vehiculoRepository.save(vehiculo));
    }

    @Override
    public void eliminarVehiculo(Long id) {
        Vehiculo vehiculo = buscarPorIdOLanzarError(id);

        if (vehiculo.getEstado() == EstadoVehiculo.ALQUILADO) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "No se puede eliminar un vehiculo que esta actualmente alquilado."
            );
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
    public boolean verificarDisponibilidad(
            Long vehiculoId,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        Vehiculo vehiculo = buscarPorIdOLanzarError(vehiculoId);

        if (!fechaInicio.isBefore(fechaFin)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de inicio debe ser anterior a la fecha de fin."
            );
        }

        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de inicio no puede ser en el pasado."
            );
        }

        /*
         * TODO: Reactivar cuando se implemente la entidad Reserva.
         *
         * La verificación de disponibilidad por rango de fechas
         * requiere consultar las reservas existentes.
         *
         * return vehiculoRepository.estaDisponibleEnFechas(
         *     vehiculoId, fechaInicio, fechaFin
         * );
         */

        // Comprobación temporal: solo considera el estado del vehículo.
        return vehiculo.getEstado() == EstadoVehiculo.DISPONIBLE;
    }


    // ── helpers ─────────────────────────────────────────────────────────────

    private Vehiculo buscarPorIdOLanzarError(Long id) {
        return vehiculoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Vehiculo con ID " + id + " no encontrado."
            ));
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
