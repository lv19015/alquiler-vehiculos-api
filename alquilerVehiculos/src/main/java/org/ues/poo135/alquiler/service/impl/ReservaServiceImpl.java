package org.ues.poo135.alquiler.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import org.ues.poo135.alquiler.rules.ReglasEstadoReserva;
import org.ues.poo135.alquiler.rules.ReglasEstadoVehiculo;
import org.ues.poo135.alquiler.service.DisponibilidadService;
import org.ues.poo135.alquiler.service.ReservaService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final DisponibilidadService disponibilidadService;

    @Override
    public ReservaResponseDTO crearReserva(CrearReservaDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cliente con ID " + dto.getClienteId() + " no encontrado."));

        Vehiculo vehiculo = vehiculoRepository.findById(dto.getVehiculoId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Vehiculo con ID " + dto.getVehiculoId() + " no encontrado."));

        // Reglas de negocio: fechas validas, vehiculo DISPONIBLE y sin traslape
        disponibilidadService.validarRangoFechas(dto.getFechaInicio(), dto.getFechaFin());
        disponibilidadService.validarParaReservar(vehiculo, dto.getFechaInicio(), dto.getFechaFin());

        Reserva reserva = Reserva.builder()
            .cliente(cliente)
            .vehiculo(vehiculo)
            .fechaInicio(dto.getFechaInicio())
            .fechaFin(dto.getFechaFin())
            .estado(EstadoReserva.ACTIVA)
            .build();
        reserva.calcularCostoTotal();

        // Regla de estado: al confirmar la reserva el vehiculo pasa a ALQUILADO
        ReglasEstadoVehiculo.validarTransicion(vehiculo.getEstado(), EstadoVehiculo.ALQUILADO);
        vehiculo.setEstado(EstadoVehiculo.ALQUILADO);
        vehiculoRepository.save(vehiculo);

        return toResponseDTO(reservaRepository.save(reserva));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerTodas() {
        return reservaRepository.findAll()
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerPorId(Long id) {
        return toResponseDTO(buscarPorIdOLanzarError(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente con ID " + clienteId + " no encontrado.");
        }
        return reservaRepository.findByClienteId(clienteId)
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    @Override
    public ReservaResponseDTO actualizarReserva(Long id, ActualizarReservaDTO dto) {
        Reserva reserva = buscarPorIdOLanzarError(id);

        ReglasEstadoReserva.validarModificable(reserva.getEstado());
        disponibilidadService.validarRangoFechas(dto.getFechaInicio(), dto.getFechaFin());

        // Se excluye la propia reserva para que no choque consigo misma
        disponibilidadService.validarSinTraslape(
            reserva.getVehiculo().getId(), dto.getFechaInicio(), dto.getFechaFin(), reserva.getId());

        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());
        reserva.calcularCostoTotal();

        return toResponseDTO(reservaRepository.save(reserva));
    }

    @Override
    public ReservaResponseDTO cancelarReserva(Long id) {
        return cerrarReserva(id, EstadoReserva.CANCELADA);
    }

    @Override
    public ReservaResponseDTO finalizarReserva(Long id) {
        return cerrarReserva(id, EstadoReserva.FINALIZADA);
    }

    @Override
    public void eliminarReserva(Long id) {
        Reserva reserva = buscarPorIdOLanzarError(id);
        ReglasEstadoReserva.validarEliminable(reserva.getEstado());
        reservaRepository.delete(reserva);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    /** Cancelar y finalizar hacen lo mismo: cierran la reserva y liberan el vehiculo. */
    private ReservaResponseDTO cerrarReserva(Long id, EstadoReserva nuevoEstado) {
        Reserva reserva = buscarPorIdOLanzarError(id);

        ReglasEstadoReserva.validarTransicion(reserva.getEstado(), nuevoEstado);
        reserva.setEstado(nuevoEstado);

        // Solo se libera si sigue ALQUILADO (no pisa un EN_MANTENIMIENTO)
        Vehiculo vehiculo = reserva.getVehiculo();
        if (vehiculo.getEstado() == EstadoVehiculo.ALQUILADO) {
            ReglasEstadoVehiculo.validarTransicion(EstadoVehiculo.ALQUILADO, EstadoVehiculo.DISPONIBLE);
            vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
            vehiculoRepository.save(vehiculo);
        }

        return toResponseDTO(reservaRepository.save(reserva));
    }

    private Reserva buscarPorIdOLanzarError(Long id) {
        return reservaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Reserva con ID " + id + " no encontrada."));
    }

    private ReservaResponseDTO toResponseDTO(Reserva r) {
        return ReservaResponseDTO.builder()
            .id(r.getId())
            .clienteId(r.getCliente().getId())
            .nombreCliente(r.getCliente().getNombre() + " " + r.getCliente().getApellido())
            .vehiculoId(r.getVehiculo().getId())
            .vehiculo(r.getVehiculo().getMarca() + " " + r.getVehiculo().getModelo())
            .placa(r.getVehiculo().getPlaca())
            .fechaInicio(r.getFechaInicio())
            .fechaFin(r.getFechaFin())
            .costoTotal(r.getCostoTotal())
            .estado(r.getEstado().name())
            .build();
    }
}
