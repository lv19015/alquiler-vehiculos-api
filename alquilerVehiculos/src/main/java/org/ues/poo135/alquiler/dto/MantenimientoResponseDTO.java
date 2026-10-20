package org.ues.poo135.alquiler.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MantenimientoResponseDTO {
    private Long id;
    private Long vehiculoId;
    private String placaVehiculo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String descripcion;
    private Double costo;
}