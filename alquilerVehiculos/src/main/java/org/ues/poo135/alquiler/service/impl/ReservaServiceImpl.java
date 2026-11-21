package org.ues.poo135.alquiler.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.ues.poo135.alquiler.dto.ActualizarReservaDTO;
import org.ues.poo135.alquiler.dto.CrearReservaDTO;
import org.ues.poo135.alquiler.dto.ReservaResponseDTO;
import org.ues.poo135.alquiler.model.Cliente;
import org.ues.poo135.alquiler.model.Reserva;
import org.ues.poo135.alquiler.model.Reserva.EstadoReserva;
import org.ues.poo135.alquiler.model.Vehiculo;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;
import org.ues.poo135.alquiler.repository.ClienteRepository;
import org.ues.poo135.alquiler.repository.ReservaRepository;
import org.ues.poo135.alquiler.repository.VehiculoRepository;
import org.ues.poo135.alquiler.service.ReservaService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaServiceImpl implements ReservaService {

    // Id que nunca existe: se usa al crear, cuando no hay reserva propia que excluir
    private static final Long SIN_EXCLUSION = -1L;

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;

    @Override
    public ReservaResponseDTO crearReserva(CrearReservaDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Cliente con ID " + dto.getClienteId() + " no encontrado."));

        Vehiculo vehiculo = vehiculoRepository.findById(dto.getVehiculoId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Vehiculo con ID " + dto.getVehiculoId() + " no encontrado."));

        validarRangoFechas(dto.getFechaInicio(), dto.getFechaFin());

        // Regla 1: el vehiculo debe estar en estado DISPONIBLE
        if (vehiculo.getEstado() != EstadoVehiculo.DISPONIBLE) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El vehiculo no esta disponible. Estado actual: " + vehiculo.getEstado() + "."
            );
        }

        // Regla 2: sin traslape de fechas con otras reservas activas
        if (reservaRepository.existeReservaActivaEnRango(
                vehiculo.getId(), dto.getFechaInicio(), dto.getFechaFin(), SIN_EXCLUSION)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El vehiculo ya tiene una reserva activa que se traslapa con esas fechas."
            );
        }

        Reserva reserva = Reserva.builder()
            .cliente(cliente)
            .vehiculo(vehiculo)
            .fechaInicio(dto.getFechaInicio())
            .fechaFin(dto.getFechaFin())
            .estado(EstadoReserva.ACTIVA)
            .build();
        reserva.calcularCostoTotal();

        // Al confirmar la reserva, el vehiculo pasa a ALQUILADO
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
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Cliente con ID " + clienteId + " no encontrado.");
        }
        return reservaRepository.findByClienteId(clienteId)
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    @Override
    public ReservaResponseDTO actualizarReserva(Long id, ActualizarReservaDTO dto) {
        Reserva reserva = buscarPorIdOLanzarError(id);

        if (reserva.getEstado() != EstadoReserva.ACTIVA) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Solo se pueden modificar reservas ACTIVAS. Estado actual: " + reserva.getEstado() + "."
            );
        }

        validarRangoFechas(dto.getFechaInicio(), dto.getFechaFin());

        // Se excluye la propia reserva para que no choque consigo misma
        if (reservaRepository.existeReservaActivaEnRango(
                reserva.getVehiculo().getId(), dto.getFechaInicio(), dto.getFechaFin(), reserva.getId())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El vehiculo ya tiene otra reserva activa que se traslapa con esas fechas."
            );
        }

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

        if (reserva.getEstado() == EstadoReserva.ACTIVA) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "No se puede eliminar una reserva ACTIVA. Cancelela o finalicela primero."
            );
        }

        reservaRepository.delete(reserva);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    /** Cancelar y finalizar hacen lo mismo: cierran la reserva y liberan el vehiculo. */
    private ReservaResponseDTO cerrarReserva(Long id, EstadoReserva nuevoEstado) {
        Reserva reserva = buscarPorIdOLanzarError(id);

        if (reserva.getEstado() != EstadoReserva.ACTIVA) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "La reserva ya esta " + reserva.getEstado() + " y no puede cambiar a " + nuevoEstado + "."
            );
        }

        reserva.setEstado(nuevoEstado);

        // Solo se libera si sigue ALQUILADO (no pisa un EN_MANTENIMIENTO)
        Vehiculo vehiculo = reserva.getVehiculo();
        if (vehiculo.getEstado() == EstadoVehiculo.ALQUILADO) {
            vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
            vehiculoRepository.save(vehiculo);
        }

        return toResponseDTO(reservaRepository.save(reserva));
    }

    private void validarRangoFechas(LocalDate inicio, LocalDate fin) {
        if (!inicio.isBefore(fin)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La fecha de inicio debe ser anterior a la fecha de fin."
            );
        }
    }

    private Reserva buscarPorIdOLanzarError(Long id) {
        return reservaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Reserva con ID " + id + " no encontrada."));
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
