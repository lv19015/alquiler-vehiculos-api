package org.ues.poo135.alquiler.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ues.poo135.alquiler.dto.ActualizarReservaDTO;
import org.ues.poo135.alquiler.dto.CrearReservaDTO;
import org.ues.poo135.alquiler.dto.ReservaResponseDTO;
import org.ues.poo135.alquiler.exception.ResourceNotFoundException;
import org.ues.poo135.alquiler.model.Cliente;
import org.ues.poo135.alquiler.model.Reserva;
import org.ues.poo135.alquiler.model.Reserva.EstadoReserva;
import org.ues.poo135.alquiler.model.Vehiculo;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;
import org.ues.poo135.alquiler.repository.ClienteRepository;
import org.ues.poo135.alquiler.repository.ReservaRepository;
import org.ues.poo135.alquiler.repository.VehiculoRepository;
import org.ues.poo135.alquiler.service.DisponibilidadService;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {
    @Mock private ReservaRepository reservaRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private VehiculoRepository vehiculoRepository;
    @Mock private DisponibilidadService disponibilidadService;
    @InjectMocks private ReservaServiceImpl reservaService;
    private Cliente cliente;
    private Vehiculo vehiculo;
    private Reserva reservaActiva;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder().id(1L).nombre("Ana").apellido("Gomez")
                .documentoIdentidad("01234567-8").correo("ana@correo.com")
                .telefono("7000-1111").licenciaConducir("LIC-111").build();
        vehiculo = Vehiculo.builder().id(1L).marca("Toyota").modelo("Corolla")
                .placa("P123-456").categoria("Sedan").precioPorDia(40.0)
                .estado(EstadoVehiculo.DISPONIBLE).build();
        reservaActiva = Reserva.builder().id(1L).cliente(cliente).vehiculo(vehiculo)
                .fechaInicio(LocalDate.now().plusDays(1)).fechaFin(LocalDate.now().plusDays(4))
                .costoTotal(120.0).estado(EstadoReserva.ACTIVA).build();
    }

    @Test
    void crearReserva_Exito() {
        CrearReservaDTO dto = new CrearReservaDTO();
        dto.setClienteId(1L); dto.setVehiculoId(1L);
        dto.setFechaInicio(LocalDate.now().plusDays(1));
        dto.setFechaFin(LocalDate.now().plusDays(4));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> { Reserva r = i.getArgument(0); r.setId(1L); return r; });
        ReservaResponseDTO res = reservaService.crearReserva(dto);
        assertEquals(120.0, res.getCostoTotal());
        assertEquals(EstadoVehiculo.ALQUILADO, vehiculo.getEstado());
    }

    @Test
    void actualizarReserva_Exito() {
        ActualizarReservaDTO dto = new ActualizarReservaDTO();
        dto.setFechaInicio(LocalDate.now().plusDays(2));
        dto.setFechaFin(LocalDate.now().plusDays(7));
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaActiva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));
        ReservaResponseDTO res = reservaService.actualizarReserva(1L, dto);
        assertEquals(200.0, res.getCostoTotal());
    }

    @Test
    void cancelarReserva_Exito() {
        vehiculo.setEstado(EstadoVehiculo.ALQUILADO);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaActiva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));
        ReservaResponseDTO res = reservaService.cancelarReserva(1L);
        assertEquals("CANCELADA", res.getEstado());
        assertEquals(EstadoVehiculo.DISPONIBLE, vehiculo.getEstado());
    }

    @Test
    void finalizarReserva_Exito() {
        vehiculo.setEstado(EstadoVehiculo.ALQUILADO);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaActiva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));
        ReservaResponseDTO res = reservaService.finalizarReserva(1L);
        assertEquals("FINALIZADA", res.getEstado());
        assertEquals(EstadoVehiculo.DISPONIBLE, vehiculo.getEstado());
    }

    @Test
    void obtenerPorCliente_NoEncontrado() {
        when(clienteRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> reservaService.obtenerPorCliente(99L));
    }
}
