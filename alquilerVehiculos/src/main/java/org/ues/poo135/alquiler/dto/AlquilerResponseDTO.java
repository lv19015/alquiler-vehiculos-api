package org.ues.poo135.alquiler.dto;

import org.ues.poo135.alquiler.model.Alquiler;
import org.ues.poo135.alquiler.model.Alquiler.EstadoAlquiler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AlquilerResponseDTO(
        Long id,
        Long clienteId,
        String clienteNombre,
        Long vehiculoId,
        String vehiculoPlaca,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal total,
        EstadoAlquiler estado,
        LocalDateTime creadoEn
) {
    public static AlquilerResponseDTO fromEntity(Alquiler alquiler) {
        return new AlquilerResponseDTO(
                alquiler.getId(),
                alquiler.getCliente().getId(),
                alquiler.getCliente().getNombre(), // Ajusta si el getter en Cliente se llama distinto
                alquiler.getVehiculo().getId(),
                alquiler.getVehiculo().getPlaca(), // Ajusta si el getter en Vehiculo se llama distinto
                alquiler.getFechaInicio(),
                alquiler.getFechaFin(),
                alquiler.getTotal(),
                alquiler.getEstado(),
                alquiler.getCreadoEn()
        );
    }
}