package org.ues.poo135.alquiler.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ues.poo135.alquiler.dto.CrearMantenimientoDTO;
import org.ues.poo135.alquiler.dto.MantenimientoResponseDTO;
import org.ues.poo135.alquiler.model.Mantenimiento;
import org.ues.poo135.alquiler.model.Vehiculo;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;
import org.ues.poo135.alquiler.repository.MantenimientoRepository;
import org.ues.poo135.alquiler.repository.VehiculoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MantenimientoServiceImplTest {
    @Mock private MantenimientoRepository mantenimientoRepository;
    @Mock private VehiculoRepository vehiculoRepository;
    @InjectMocks private MantenimientoServiceImpl mantenimientoService;
    private Vehiculo vehiculo;
    private Mantenimiento mantenimiento;
    private CrearMantenimientoDTO dto;

    @BeforeEach
    void setUp() {
        vehiculo = Vehiculo.builder().id(1L).marca("Nissan").modelo("Frontier")
                .placa("P555-111").categoria("Pickup").precioPorDia(70.0)
                .estado(EstadoVehiculo.DISPONIBLE).build();
        mantenimiento = Mantenimiento.builder().id(1L).vehiculo(vehiculo)
                .fechaInicio(LocalDate.now()).fechaFin(LocalDate.now().plusDays(2))
                .descripcion("Cambio de aceite").costo(85.0).build();
        dto = CrearMantenimientoDTO.builder().vehiculoId(1L)
                .fechaInicio(LocalDate.now()).fechaFin(LocalDate.now().plusDays(2))
                .descripcion("Cambio de aceite").costo(85.0).build();
    }

    @Test
    void registrar_Exito() {
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenReturn(mantenimiento);
        MantenimientoResponseDTO res = mantenimientoService.registrar(dto);
        assertEquals(EstadoVehiculo.EN_MANTENIMIENTO, vehiculo.getEstado());
        assertEquals("P555-111", res.getPlacaVehiculo());
    }

    @Test
    void registrar_VehiculoNoEncontrado() {
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> mantenimientoService.registrar(dto));
    }

    @Test
    void listarTodos_Exito() {
        when(mantenimientoRepository.findAll()).thenReturn(List.of(mantenimiento));
        assertEquals(1, mantenimientoService.listarTodos().size());
    }

    @Test
    void eliminar_Exito() {
        when(mantenimientoRepository.findById(1L)).thenReturn(Optional.of(mantenimiento));
        mantenimientoService.eliminar(1L);
        verify(mantenimientoRepository).delete(mantenimiento);
    }
}
