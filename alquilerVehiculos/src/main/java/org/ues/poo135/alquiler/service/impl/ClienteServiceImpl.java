package org.ues.poo135.alquiler.service.impl; // (o org.ues.poo135.alquiler.service según tu paquete)

import org.ues.poo135.alquiler.dto.CrearClienteDTO;
import org.ues.poo135.alquiler.dto.ClienteResponseDTO;
import org.ues.poo135.alquiler.model.Cliente;
import org.ues.poo135.alquiler.repository.ClienteRepository;
import org.ues.poo135.alquiler.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public ClienteResponseDTO crearCliente(CrearClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setDui(dto.getDui());
        cliente.setCorreo(dto.getCorreo());
        cliente.setTelefono(dto.getTelefono());

        Cliente clienteGuardado = clienteRepository.save(cliente);
        return mapearARespuesta(clienteGuardado);
    }

    @Override
    public List<ClienteResponseDTO> listarClientes() {
        return clienteRepository.findAll().stream()
                .map(this::mapearARespuesta)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
        return mapearARespuesta(cliente);
    }

    @Override
    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    private ClienteResponseDTO mapearARespuesta(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setDui(cliente.getDui());
        dto.setCorreo(cliente.getCorreo());
        dto.setTelefono(cliente.getTelefono());
        return dto;
    }
}