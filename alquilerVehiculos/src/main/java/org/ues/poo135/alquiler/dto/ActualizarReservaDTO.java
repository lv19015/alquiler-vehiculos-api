package org.ues.poo135.alquiler.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * Solo permite modificar el rango de fechas. Para cambiar de cliente
 * o de vehiculo se cancela la reserva y se crea una nueva.
 */
@Data
public class ActualizarReservaDTO {

    @NotNull(message = "La fecha de inicio es obligatoria.")
    @FutureOrPresent(message = "La fecha de inicio no puede estar en el pasado.")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria.")
    @Future(message = "La fecha de fin debe ser futura.")
    private LocalDate fechaFin;
}
