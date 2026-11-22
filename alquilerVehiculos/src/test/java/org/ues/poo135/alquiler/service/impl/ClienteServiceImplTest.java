package org.ues.poo135.alquiler.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.ues.poo135.alquiler.dto.ClienteResponseDTO;
import org.ues.poo135.alquiler.dto.CrearClienteDTO;
import org.ues.poo135.alquiler.exception.ResourceNotFoundException;
import org.ues.poo135.alquiler.model.Cliente;
import org.ues.poo135.alquiler.repository.ClienteRepository;
import org.ues.poo135.alquiler.repository.ReservaRepository;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {
    @Mock private ClienteRepository clienteRepository;
    @Mock private ReservaRepository reservaRepository;
    @InjectMocks private ClienteServiceImpl clienteService;
    private Cliente cliente;
    private CrearClienteDTO dto;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder().id(1L).nombre("Carlos").apellido("Mendoza")
                .documentoIdentidad("04567890-1").correo("carlos@correo.com")
                .telefono("7777-1234").licenciaConducir("LIC-9988").build();
        dto = new CrearClienteDTO();
        dto.setNombre("Carlos"); dto.setApellido("Mendoza"); dto.setDocumentoIdentidad("04567890-1");
        dto.setCorreo("carlos@correo.com"); dto.setTelefono("7777-1234"); dto.setLicenciaConducir("LIC-9988");
    }

    @Test
    void crearCliente_Exito() {
        when(clienteRepository.existsByDocumentoIdentidad(dto.getDocumentoIdentidad())).thenReturn(false);
        when(clienteRepository.existsByCorreoIgnoreCase(dto.getCorreo())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        ClienteResponseDTO res = clienteService.crearCliente(dto);
        assertEquals("Carlos", res.getNombre());
    }

    @Test
    void crearCliente_Duplicado() {
        when(clienteRepository.existsByDocumentoIdentidad(dto.getDocumentoIdentidad())).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> clienteService.crearCliente(dto));
    }

    @Test
    void obtenerTodos_Exito() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));
        assertEquals(1, clienteService.obtenerTodos().size());
    }

    @Test
    void obtenerPorId_NoEncontrado() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> clienteService.obtenerPorId(99L));
    }

    @Test
    void eliminarCliente_ConReservas() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(reservaRepository.existsByClienteId(1L)).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> clienteService.eliminarCliente(1L));
    }
}
