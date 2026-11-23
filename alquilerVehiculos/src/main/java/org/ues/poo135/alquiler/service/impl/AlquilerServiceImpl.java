package org.ues.poo135.alquiler.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ues.poo135.alquiler.dto.AlquilerResponseDTO;
import org.ues.poo135.alquiler.dto.CrearAlquilerDTO;
import org.ues.poo135.alquiler.exception.ResourceNotFoundException;
import org.ues.poo135.alquiler.model.Alquiler;
import org.ues.poo135.alquiler.model.Alquiler.EstadoAlquiler;
import org.ues.poo135.alquiler.model.Cliente;
import org.ues.poo135.alquiler.model.Vehiculo;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;
import org.ues.poo135.alquiler.repository.AlquilerRepository;
import org.ues.poo135.alquiler.repository.ClienteRepository;
import org.ues.poo135.alquiler.repository.VehiculoRepository;
import org.ues.poo135.alquiler.rules.ReglasEstadoVehiculo;
import org.ues.poo135.alquiler.service.AlquilerService;
import org.ues.poo135.alquiler.service.DisponibilidadService;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlquilerServiceImpl implements AlquilerService {

    private final AlquilerRepository alquilerRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final DisponibilidadService disponibilidadService;

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerResponseDTO> listarTodos() {
        return alquilerRepository.findAll()
                .stream()
                .map(AlquilerResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlquilerResponseDTO obtenerPorId(Long id) {
        return AlquilerResponseDTO.fromEntity(buscarPorIdOLanzarError(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerResponseDTO> listarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente con ID " + clienteId + " no encontrado.");
        }
        return alquilerRepository.findByClienteId(clienteId)
                .stream()
                .map(AlquilerResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerResponseDTO> listarPorVehiculo(Long vehiculoId) {
        if (!vehiculoRepository.existsById(vehiculoId)) {
            throw new ResourceNotFoundException("Vehículo con ID " + vehiculoId + " no encontrado.");
        }
        return alquilerRepository.findByVehiculoId(vehiculoId)
                .stream()
                .map(AlquilerResponseDTO::fromEntity)
                .toList();
    }

    @Override
    public AlquilerResponseDTO crear(CrearAlquilerDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente con ID " + dto.getClienteId() + " no encontrado."));

        Vehiculo vehiculo = vehiculoRepository.findById(dto.getVehiculoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehículo con ID " + dto.getVehiculoId() + " no encontrado."));

        // Validaciones de negocio de fechas y disponibilidad
        disponibilidadService.validarRangoFechas(dto.getFechaInicio(), dto.getFechaFin());
        disponibilidadService.validarParaReservar(vehiculo, dto.getFechaInicio(), dto.getFechaFin());

        long dias = ChronoUnit.DAYS.between(dto.getFechaInicio(), dto.getFechaFin());
        if (dias <= 0) {
            dias = 1;
        }
        BigDecimal precioDiario = BigDecimal.valueOf(vehiculo.getPrecioPorDia());
        BigDecimal total = precioDiario.multiply(BigDecimal.valueOf(dias));

        Alquiler alquiler = Alquiler.builder()
                .cliente(cliente)
                .vehiculo(vehiculo)
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .total(total)
                .estado(EstadoAlquiler.ACTIVO)
                .build();

        // Cambiar estado del vehículo a ALQUILADO
        ReglasEstadoVehiculo.validarTransicion(vehiculo.getEstado(), EstadoVehiculo.ALQUILADO);
        vehiculo.setEstado(EstadoVehiculo.ALQUILADO);
        vehiculoRepository.save(vehiculo);

        return AlquilerResponseDTO.fromEntity(alquilerRepository.save(alquiler));
    }

    @Override
    public AlquilerResponseDTO finalizar(Long id) {
        return cerrarAlquiler(id, EstadoAlquiler.FINALIZADO);
    }

    @Override
    public AlquilerResponseDTO cancelar(Long id) {
        return cerrarAlquiler(id, EstadoAlquiler.CANCELADO);
    }

    @Override
    public void eliminar(Long id) {
        Alquiler alquiler = buscarPorIdOLanzarError(id);
        if (alquiler.getEstado() == EstadoAlquiler.ACTIVO) {
            throw new IllegalStateException("No se puede eliminar un alquiler activo. Debe finalizarlo o cancelarlo primero.");
        }
        alquilerRepository.delete(alquiler);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private AlquilerResponseDTO cerrarAlquiler(Long id, EstadoAlquiler nuevoEstado) {
        Alquiler alquiler = buscarPorIdOLanzarError(id);

        if (alquiler.getEstado() != EstadoAlquiler.ACTIVO) {
            throw new IllegalStateException("Solo se pueden finalizar o cancelar alquileres en estado ACTIVO.");
        }

        alquiler.setEstado(nuevoEstado);

        Vehiculo vehiculo = alquiler.getVehiculo();
        if (vehiculo.getEstado() == EstadoVehiculo.ALQUILADO) {
            ReglasEstadoVehiculo.validarTransicion(EstadoVehiculo.ALQUILADO, EstadoVehiculo.DISPONIBLE);
            vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
            vehiculoRepository.save(vehiculo);
        }

        return AlquilerResponseDTO.fromEntity(alquilerRepository.save(alquiler));
    }

    private Alquiler buscarPorIdOLanzarError(Long id) {
        return alquilerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Alquiler con ID " + id + " no encontrado."));
    }
}
