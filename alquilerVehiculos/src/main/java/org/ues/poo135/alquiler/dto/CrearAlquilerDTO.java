package org.ues.poo135.alquiler.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearAlquilerDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    @Positive(message = "El ID del cliente debe ser un número válido")
    private Long clienteId;

    @NotNull(message = "El ID del vehículo es obligatorio")
    @Positive(message = "El ID del vehículo debe ser un número válido")
    private Long vehiculoId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede ser anterior a hoy")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @FutureOrPresent(message = "La fecha de fin debe ser hoy o posterior")
    private LocalDate fechaFin;

    @AssertTrue(message = "La fecha de fin debe ser igual o posterior a la fecha de inicio")
    public boolean isRangoFechasValido() {
        if (fechaInicio == null || fechaFin == null) {
            return true; // Deja que @NotNull maneje los nulos
        }
        return !fechaFin.isBefore(fechaInicio);
    }
}