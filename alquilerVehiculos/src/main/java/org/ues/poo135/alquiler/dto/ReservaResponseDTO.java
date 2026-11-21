package org.ues.poo135.alquiler.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReservaResponseDTO {

    private Long id;
    private Long clienteId;
    private String nombreCliente;
    private Long vehiculoId;
    private String vehiculo;      // "Marca Modelo"
    private String placa;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double costoTotal;
    private String estado;        // ACTIVA | FINALIZADA | CANCELADA
}
