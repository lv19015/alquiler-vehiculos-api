package org.ues.poo135.alquiler.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ues.poo135.alquiler.exception.BadRequestException;
import org.ues.poo135.alquiler.exception.ConflictException;
import org.ues.poo135.alquiler.model.Vehiculo;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;
import org.ues.poo135.alquiler.repository.ReservaRepository;
import org.ues.poo135.alquiler.service.DisponibilidadService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DisponibilidadServiceImpl implements DisponibilidadService {

    // Id que nunca existe: se usa cuando no hay reserva propia que excluir
    private static final Long SIN_EXCLUSION = -1L;

    private final ReservaRepository reservaRepository;

    @Override
    public void validarRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BadRequestException("Las fechas de inicio y fin son obligatorias.");
        }
        if (!fechaInicio.isBefore(fechaFin)) {
            throw new BadRequestException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
    }

    @Override
    public boolean estaDisponible(Vehiculo vehiculo, LocalDate fechaInicio, LocalDate fechaFin) {
        return vehiculo.getEstado() == EstadoVehiculo.DISPONIBLE
                && !haySolapamiento(vehiculo.getId(), fechaInicio, fechaFin, SIN_EXCLUSION);
    }

    @Override
    public void validarParaReservar(Vehiculo vehiculo, LocalDate fechaInicio, LocalDate fechaFin) {
        if (vehiculo.getEstado() != EstadoVehiculo.DISPONIBLE) {
            throw new ConflictException(
                    "El vehiculo no esta disponible. Estado actual: " + vehiculo.getEstado() + ".");
        }
        validarSinTraslape(vehiculo.getId(), fechaInicio, fechaFin, SIN_EXCLUSION);
    }

    @Override
    public void validarSinTraslape(Long vehiculoId, LocalDate fechaInicio, LocalDate fechaFin, Long excluirReservaId) {
        if (haySolapamiento(vehiculoId, fechaInicio, fechaFin, excluirReservaId)) {
            throw new ConflictException(
                    "El vehiculo ya tiene una reserva activa que se traslapa con esas fechas.");
        }
    }

    private boolean haySolapamiento(Long vehiculoId, LocalDate inicio, LocalDate fin, Long excluirId) {
        return reservaRepository.existeReservaActivaEnRango(vehiculoId, inicio, fin, excluirId);
    }
}
