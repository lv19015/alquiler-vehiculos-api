package org.ues.poo135.alquiler.service;

import org.ues.poo135.alquiler.model.Vehiculo;

import java.time.LocalDate;

/**
 * Regla de negocio central: cuando un vehiculo se puede reservar.
 * Un vehiculo esta disponible en un rango si su estado es DISPONIBLE
 * y no tiene reservas ACTIVAS que se traslapen con esas fechas.
 */
public interface DisponibilidadService {

    /** fechaInicio debe ser anterior a fechaFin. Lanza BadRequestException si no. */
    void validarRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);

    /** Consulta: no lanza excepciones por falta de disponibilidad. */
    boolean estaDisponible(Vehiculo vehiculo, LocalDate fechaInicio, LocalDate fechaFin);

    /** Para crear una reserva: valida estado y traslape. Lanza ConflictException con el motivo. */
    void validarParaReservar(Vehiculo vehiculo, LocalDate fechaInicio, LocalDate fechaFin);

    /** Para modificar una reserva: solo traslape, ignorando la propia reserva. */
    void validarSinTraslape(Long vehiculoId, LocalDate fechaInicio, LocalDate fechaFin, Long excluirReservaId);
}
