package org.ues.poo135.alquiler.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.ues.poo135.alquiler.service.DisponibilidadService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlquilerServiceImplTest {

    @Mock private AlquilerRepository alquilerRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private VehiculoRepository vehiculoRepository;
    @Mock private DisponibilidadService disponibilidadService;

    @InjectMocks private AlquilerServiceImpl alquilerService;

    private Cliente cliente;
    private Vehiculo vehiculo;
    private Alquiler alquilerActivo;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nombre("Ana")
                .apellido("Gomez")
                .documentoIdentidad("01234567-8")
                .correo("ana@correo.com")
                .telefono("7000-1111")
                .licenciaConducir("LIC-111")
                .build();

        vehiculo = Vehiculo.builder()
                .id(1L)
                .marca("Toyota")
                .modelo("Corolla")
                .placa("P123-456")
                .categoria("Sedan")
                .precioPorDia(40.0)
                .estado(EstadoVehiculo.DISPONIBLE)
                .build();

        alquilerActivo = Alquiler.builder()
                .id(1L)
                .cliente(cliente)
                .vehiculo(vehiculo)
                .fechaInicio(LocalDate.now().plusDays(1))
                .fechaFin(LocalDate.now().plusDays(4))
                .total(BigDecimal.valueOf(120.0))
                .estado(EstadoAlquiler.ACTIVO)
                .build();
    }

    @Test
    void crearAlquiler_Exito() {
        CrearAlquilerDTO dto = new CrearAlquilerDTO();
        dto.setClienteId(1L);
        dto.setVehiculoId(1L);
        dto.setFechaInicio(LocalDate.now().plusDays(1));
        dto.setFechaFin(LocalDate.now().plusDays(4));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(alquilerRepository.save(any(Alquiler.class))).thenAnswer(i -> {
            Alquiler a = i.getArgument(0);
            a.setId(1L);
            return a;
        });

        AlquilerResponseDTO res = alquilerService.crear(dto);

        assertEquals(0, BigDecimal.valueOf(120.0).compareTo(res.total()));
        assertEquals(EstadoAlquiler.ACTIVO, res.estado());
        assertEquals(EstadoVehiculo.ALQUILADO, vehiculo.getEstado());
        verify(vehiculoRepository).save(vehiculo);
    }

    @Test
    void crearAlquiler_ClienteNoEncontrado() {
        CrearAlquilerDTO dto = new CrearAlquilerDTO();
        dto.setClienteId(99L);
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alquilerService.crear(dto));
    }

    @Test
    void finalizarAlquiler_Exito() {
        vehiculo.setEstado(EstadoVehiculo.ALQUILADO);
        when(alquilerRepository.findById(1L)).thenReturn(Optional.of(alquilerActivo));
        when(alquilerRepository.save(any(Alquiler.class))).thenAnswer(i -> i.getArgument(0));

        AlquilerResponseDTO res = alquilerService.finalizar(1L);

        assertEquals(EstadoAlquiler.FINALIZADO, res.estado());
        assertEquals(EstadoVehiculo.DISPONIBLE, vehiculo.getEstado());
    }

    @Test
    void cancelarAlquiler_Exito() {
        vehiculo.setEstado(EstadoVehiculo.ALQUILADO);
        when(alquilerRepository.findById(1L)).thenReturn(Optional.of(alquilerActivo));
        when(alquilerRepository.save(any(Alquiler.class))).thenAnswer(i -> i.getArgument(0));

        AlquilerResponseDTO res = alquilerService.cancelar(1L);

        assertEquals(EstadoAlquiler.CANCELADO, res.estado());
        assertEquals(EstadoVehiculo.DISPONIBLE, vehiculo.getEstado());
    }

    @Test
    void eliminarAlquiler_ActivoLanzaExcepcion() {
        when(alquilerRepository.findById(1L)).thenReturn(Optional.of(alquilerActivo));

        assertThrows(IllegalStateException.class, () -> alquilerService.eliminar(1L));
        verify(alquilerRepository, never()).delete(any());
    }

    @Test
    void eliminarAlquiler_FinalizadoExito() {
        alquilerActivo.setEstado(EstadoAlquiler.FINALIZADO);
        when(alquilerRepository.findById(1L)).thenReturn(Optional.of(alquilerActivo));

        alquilerService.eliminar(1L);

        verify(alquilerRepository).delete(alquilerActivo);
    }

    @Test
    void listarPorCliente_NoEncontrado() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> alquilerService.listarPorCliente(99L));
    }

    @Test
    void listarTodos_Exito() {
        when(alquilerRepository.findAll()).thenReturn(List.of(alquilerActivo));

        List<AlquilerResponseDTO> lista = alquilerService.listarTodos();

        assertEquals(1, lista.size());
        assertEquals("P123-456", lista.get(0).vehiculoPlaca());
    }
}

