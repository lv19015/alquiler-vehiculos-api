package org.ues.poo135.alquiler.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ues.poo135.alquiler.dto.CrearMantenimientoDTO;
import org.ues.poo135.alquiler.dto.MantenimientoResponseDTO;
import org.ues.poo135.alquiler.model.Mantenimiento;
import org.ues.poo135.alquiler.model.Vehiculo;
import org.ues.poo135.alquiler.repository.MantenimientoRepository;
import org.ues.poo135.alquiler.repository.VehiculoRepository;
import org.ues.poo135.alquiler.service.MantenimientoService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MantenimientoServiceImpl implements MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final VehiculoRepository vehiculoRepository;

    @Override
    @Transactional
    public MantenimientoResponseDTO registrar(CrearMantenimientoDTO dto) {
        Vehiculo vehiculo = vehiculoRepository.findById(dto.getVehiculoId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + dto.getVehiculoId()));

        vehiculo.setEstado(Vehiculo.EstadoVehiculo.EN_MANTENIMIENTO);
        vehiculoRepository.save(vehiculo);

        Mantenimiento mantenimiento = Mantenimiento.builder()
                .vehiculo(vehiculo)
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .descripcion(dto.getDescripcion())
                .costo(dto.getCosto())
                .build();

        Mantenimiento guardado = mantenimientoRepository.save(mantenimiento);
        return mapToDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoResponseDTO> listarTodos() {
        return mantenimientoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MantenimientoResponseDTO buscarPorId(Long id) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de mantenimiento no encontrado con ID: " + id));
        return mapToDTO(mantenimiento);
    }

    @Override
    @Transactional
    public MantenimientoResponseDTO actualizar(Long id, CrearMantenimientoDTO dto) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado con ID: " + id));

        Vehiculo vehiculo = vehiculoRepository.findById(dto.getVehiculoId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + dto.getVehiculoId()));

        mantenimiento.setVehiculo(vehiculo);
        mantenimiento.setFechaInicio(dto.getFechaInicio());
        mantenimiento.setFechaFin(dto.getFechaFin());
        mantenimiento.setDescripcion(dto.getDescripcion());
        mantenimiento.setCosto(dto.getCosto());

        return mapToDTO(mantenimientoRepository.save(mantenimiento));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado con ID: " + id));
        mantenimientoRepository.delete(mantenimiento);
    }

    private MantenimientoResponseDTO mapToDTO(Mantenimiento m) {
        return MantenimientoResponseDTO.builder()
                .id(m.getId())
                .vehiculoId(m.getVehiculo().getId())
                .placaVehiculo(m.getVehiculo().getPlaca())
                .fechaInicio(m.getFechaInicio())
                .fechaFin(m.getFechaFin())
                .descripcion(m.getDescripcion())
                .costo(m.getCosto())
                .build();
    }
}