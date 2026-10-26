package org.ues.poo135.alquiler.service;

import org.ues.poo135.alquiler.dto.ClienteResponseDTO;
import org.ues.poo135.alquiler.dto.CrearClienteDTO;

import java.util.List;

public interface ClienteService {

    ClienteResponseDTO crearCliente(CrearClienteDTO dto);

    List<ClienteResponseDTO> obtenerTodos();

    ClienteResponseDTO obtenerPorId(Long id);

    ClienteResponseDTO obtenerPorDocumento(String documentoIdentidad);

    ClienteResponseDTO actualizarCliente(Long id, CrearClienteDTO dto);

    void eliminarCliente(Long id);
}