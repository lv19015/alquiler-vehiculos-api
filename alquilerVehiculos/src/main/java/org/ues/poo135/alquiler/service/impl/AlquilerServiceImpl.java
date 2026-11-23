package org.ues.poo135.alquiler.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ues.poo135.alquiler.dto.AlquilerResponseDTO;
import org.ues.poo135.alquiler.dto.CrearAlquilerDTO;
import org.ues.poo135.alquiler.exception.BadRequestException;
import org.ues.poo135.alquiler.exception.ConflictException;
import org.ues.poo135.alquiler.exception.ResourceNotFoundException;
import org.ues.poo135.alquiler.model.Alquiler;
import org.ues.poo135.alquiler.model.Alquiler.EstadoAlquiler;
import org.ues.poo135.alquiler.model.Cliente;
import org.ues.poo135.alquiler.model.Vehiculo;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;
import org.ues.poo135.alquiler.repository.AlquilerRepository;
import org.ues.poo135.alquiler.repository.ClienteRepository;
import org.ues.poo135.alquiler.repository.VehiculoRepository;
import org.ues.poo135.alquiler.service.AlquilerService;

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

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerResponseDTO> listarTodos() {
        return alquilerRepository.findAll().stream()
                .map(AlquilerResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlquilerResponseDTO obtenerPorId(Long id) {
        Alquiler alquiler = buscarPorIdOLanzar(id);
        return AlquilerResponseDTO.fromEntity(alquiler);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerResponseDTO> listarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente con ID " + clienteId + " no encontrado");
        }
        return alquilerRepository.findByClienteIdConDetalles(clienteId).stream()
                .map(AlquilerResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerResponseDTO> listarPorVehiculo(Long vehiculoId) {
        if (!vehiculoRepository.existsById(vehiculoId)) {
            throw new ResourceNotFoundException("Vehículo con ID " + vehiculoId + " no encontrado");
        }
        return alquilerRepository.findByVehiculoIdConDetalles(vehiculoId).stream()
                .map(AlquilerResponseDTO::fromEntity)
                .toList();
    }

    @Override
    public AlquilerResponseDTO crear(CrearAlquilerDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + dto.getClienteId()));

        Vehiculo vehiculo = vehiculoRepository.findById(dto.getVehiculoId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con ID: " + dto.getVehiculoId()));

        if (alquilerRepository.existeConflictoDeFechas(dto.getVehiculoId(), dto.getFechaInicio(), dto.getFechaFin())) {
            throw new ConflictException("El vehículo ya se encuentra alquilado en el rango de fechas seleccionado");
        }

        long dias = ChronoUnit.DAYS.between(dto.getFechaInicio(), dto.getFechaFin()) + 1;
        BigDecimal total = BigDecimal.valueOf(vehiculo.getPrecioPorDia() * dias);

        Alquiler alquiler = Alquiler.builder()
                .cliente(cliente)
                .vehiculo(vehiculo)
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .total(total)
                .estado(EstadoAlquiler.ACTIVO)
                .build();

        vehiculo.setEstado(EstadoVehiculo.ALQUILADO);
        vehiculoRepository.save(vehiculo);

        return AlquilerResponseDTO.fromEntity(alquilerRepository.save(alquiler));
    }

    @Override
    public AlquilerResponseDTO finalizar(Long id) {
        Alquiler alquiler = buscarPorIdOLanzar(id);
        if (alquiler.getEstado() != EstadoAlquiler.ACTIVO) {
            throw new BadRequestException("Solo se pueden finalizar alquileres que estén activos");
        }
        alquiler.setEstado(EstadoAlquiler.FINALIZADO);
        alquiler.getVehiculo().setEstado(EstadoVehiculo.DISPONIBLE);
        vehiculoRepository.save(alquiler.getVehiculo());
        return AlquilerResponseDTO.fromEntity(alquilerRepository.save(alquiler));
    }

    @Override
    public AlquilerResponseDTO cancelar(Long id) {
        Alquiler alquiler = buscarPorIdOLanzar(id);
        if (alquiler.getEstado() != EstadoAlquiler.ACTIVO) {
            throw new BadRequestException("Solo se pueden cancelar alquileres que estén activos");
        }
        alquiler.setEstado(EstadoAlquiler.CANCELADO);
        alquiler.getVehiculo().setEstado(EstadoVehiculo.DISPONIBLE);
        vehiculoRepository.save(alquiler.getVehiculo());
        return AlquilerResponseDTO.fromEntity(alquilerRepository.save(alquiler));
    }

    @Override
    public void eliminar(Long id) {
        Alquiler alquiler = buscarPorIdOLanzar(id);
        alquilerRepository.delete(alquiler);
    }

    private Alquiler buscarPorIdOLanzar(Long id) {
        return alquilerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alquiler con ID " + id + " no encontrado"));
    }
}