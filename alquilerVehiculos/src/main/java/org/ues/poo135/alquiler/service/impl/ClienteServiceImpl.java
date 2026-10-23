package org.ues.poo135.alquiler.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ues.poo135.alquiler.dto.ClienteResponseDTO;
import org.ues.poo135.alquiler.dto.CrearClienteDTO;
import org.ues.poo135.alquiler.exception.ResourceNotFoundException;
import org.ues.poo135.alquiler.model.Cliente;
import org.ues.poo135.alquiler.repository.ClienteRepository;
import org.ues.poo135.alquiler.service.ClienteService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    public ClienteResponseDTO crearCliente(CrearClienteDTO dto) {
        validarUnicidad(dto.getDocumentoIdentidad(), dto.getCorreo());

        Cliente cliente = Cliente.builder()
            .nombre(dto.getNombre())
            .apellido(dto.getApellido())
            .documentoIdentidad(dto.getDocumentoIdentidad())
            .correo(dto.getCorreo())
            .telefono(dto.getTelefono())
            .licenciaConducir(dto.getLicenciaConducir())
            .build();

        return toResponseDTO(clienteRepository.save(cliente));
    }

    @Override
    public List<ClienteResponseDTO> obtenerTodos() {
        return clienteRepository.findAll()
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    @Override
    public ClienteResponseDTO obtenerPorId(Long id) {
        return toResponseDTO(buscarPorIdOLanzarError(id));
    }

    @Override
    public ClienteResponseDTO obtenerPorDocumento(String documentoIdentidad) {
        Cliente cliente = clienteRepository.findByDocumentoIdentidad(documentoIdentidad)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cliente con documento " + documentoIdentidad + " no encontrado."));
        return toResponseDTO(cliente);
    }

    @Override
    public ClienteResponseDTO actualizarCliente(Long id, CrearClienteDTO dto) {
        Cliente cliente = buscarPorIdOLanzarError(id);

        // Solo validar unicidad si el documento o el correo cambiaron
        if (!cliente.getDocumentoIdentidad().equals(dto.getDocumentoIdentidad())
                && clienteRepository.existsByDocumentoIdentidad(dto.getDocumentoIdentidad())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El documento " + dto.getDocumentoIdentidad() + " ya esta registrado en otro cliente."
            );
        }
        if (!cliente.getCorreo().equalsIgnoreCase(dto.getCorreo())
                && clienteRepository.existsByCorreoIgnoreCase(dto.getCorreo())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El correo " + dto.getCorreo() + " ya esta registrado en otro cliente."
            );
        }

        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        cliente.setCorreo(dto.getCorreo());
        cliente.setTelefono(dto.getTelefono());
        cliente.setLicenciaConducir(dto.getLicenciaConducir());

        return toResponseDTO(clienteRepository.save(cliente));
    }

    @Override
    public void eliminarCliente(Long id) {
        Cliente cliente = buscarPorIdOLanzarError(id);

        /*
         * TODO: Reactivar cuando se implemente la entidad Reserva.
         * No se debe eliminar un cliente que tenga reservas activas:
         *
         * if (reservaRepository.existsByClienteIdAndEstado(id, EstadoReserva.CONFIRMADA)) {
         *     throw new ResponseStatusException(HttpStatus.CONFLICT,
         *         "No se puede eliminar un cliente con reservas activas.");
         * }
         */

        clienteRepository.delete(cliente);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private void validarUnicidad(String documentoIdentidad, String correo) {
        if (clienteRepository.existsByDocumentoIdentidad(documentoIdentidad)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe un cliente registrado con el documento: " + documentoIdentidad
            );
        }
        if (clienteRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe un cliente registrado con el correo: " + correo
            );
        }
    }

    private Cliente buscarPorIdOLanzarError(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cliente con ID " + id + " no encontrado."));
    }

    private ClienteResponseDTO toResponseDTO(Cliente c) {
        return ClienteResponseDTO.builder()
            .id(c.getId())
            .nombre(c.getNombre())
            .apellido(c.getApellido())
            .documentoIdentidad(c.getDocumentoIdentidad())
            .correo(c.getCorreo())
            .telefono(c.getTelefono())
            .licenciaConducir(c.getLicenciaConducir())
            .build();
    }
}
