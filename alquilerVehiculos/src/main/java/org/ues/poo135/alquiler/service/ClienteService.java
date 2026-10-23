package org.ues.poo135.alquiler.service;

import org.ues.poo135.alquiler.dto.CrearClienteDTO;
import org.ues.poo135.alquiler.dto.ClienteResponseDTO;

import java.util.List;

public interface ClienteService {
    ClienteResponseDTO crearCliente(CrearClienteDTO dto);
    List<ClienteResponseDTO> listarClientes();
    ClienteResponseDTO buscarPorId(Long id);
    void eliminarCliente(Long id);
}
