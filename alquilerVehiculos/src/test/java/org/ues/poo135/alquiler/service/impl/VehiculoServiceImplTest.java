package org.ues.poo135.alquiler.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ues.poo135.alquiler.dto.CrearVehiculoDTO;
import org.ues.poo135.alquiler.dto.VehiculoResponseDTO;
import org.ues.poo135.alquiler.exception.ConflictException;
import org.ues.poo135.alquiler.exception.ResourceNotFoundException;
import org.ues.poo135.alquiler.model.Reserva.EstadoReserva;
import org.ues.poo135.alquiler.model.Vehiculo;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;
import org.ues.poo135.alquiler.repository.ReservaRepository;
import org.ues.poo135.alquiler.repository.VehiculoRepository;
import org.ues.poo135.alquiler.service.DisponibilidadService;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiculoServiceImplTest {
    @Mock private VehiculoRepository vehiculoRepository;
    @Mock private ReservaRepository reservaRepository;
    @Mock private DisponibilidadService disponibilidadService;
    @InjectMocks private VehiculoServiceImpl vehiculoService;
    private Vehiculo vehiculo;
    private CrearVehiculoDTO dto;

    @BeforeEach
    void setUp() {
        vehiculo = Vehiculo.builder().id(1L).marca("Honda").modelo("Civic")
                .placa("P987-654").categoria("Sedan").precioPorDia(50.0)
                .estado(EstadoVehiculo.DISPONIBLE).build();
        dto = new CrearVehiculoDTO();
        dto.setMarca("Honda"); dto.setModelo("Civic"); dto.setPlaca("P987-654");
        dto.setCategoria("Sedan"); dto.setPrecioPorDia(50.0);
    }

    @Test
    void crearVehiculo_Exito() {
        when(vehiculoRepository.existsByPlaca("P987-654")).thenReturn(false);
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo);
        VehiculoResponseDTO res = vehiculoService.crearVehiculo(dto);
        assertEquals("DISPONIBLE", res.getEstado());
    }

    @Test
    void crearVehiculo_PlacaDuplicada() {
        when(vehiculoRepository.existsByPlaca("P987-654")).thenReturn(true);
        assertThrows(ConflictException.class, () -> vehiculoService.crearVehiculo(dto));
    }

    @Test
    void cambiarEstado_ConReservaActiva() {
        vehiculo.setEstado(EstadoVehiculo.ALQUILADO);
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(reservaRepository.existsByVehiculoIdAndEstado(1L, EstadoReserva.ACTIVA)).thenReturn(true);
        assertThrows(ConflictException.class, () -> vehiculoService.cambiarEstado(1L, EstadoVehiculo.DISPONIBLE));
    }

    @Test
    void verificarDisponibilidad_Exito() {
        LocalDate ini = LocalDate.now().plusDays(1);
        LocalDate fin = LocalDate.now().plusDays(3);
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(disponibilidadService.estaDisponible(vehiculo, ini, fin)).thenReturn(true);
        assertTrue(vehiculoService.verificarDisponibilidad(1L, ini, fin));
    }

    @Test
    void obtenerPorId_NoEncontrado() {
        when(vehiculoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> vehiculoService.obtenerPorId(99L));
    }
}
