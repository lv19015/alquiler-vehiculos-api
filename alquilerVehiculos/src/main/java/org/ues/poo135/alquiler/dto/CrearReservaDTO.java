package org.ues.poo135.alquiler.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CrearReservaDTO {

    @NotNull(message = "El id del cliente es obligatorio.")
    private Long clienteId;

    @NotNull(message = "El id del vehiculo es obligatorio.")
    private Long vehiculoId;

    @NotNull(message = "La fecha de inicio es obligatoria.")
    @FutureOrPresent(message = "La fecha de inicio no puede estar en el pasado.")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria.")
    @Future(message = "La fecha de fin debe ser futura.")
    private LocalDate fechaFin;
}
