package org.ues.poo135.alquiler.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehiculoResponseDTO {

    private Long id;
    private String marca;
    private String modelo;
    private String placa;
    private String categoria;
    private Double precioPorDia;
    private String estado; // DISPONIBLE | ALQUILADO | EN_MANTENIMIENTO
}
