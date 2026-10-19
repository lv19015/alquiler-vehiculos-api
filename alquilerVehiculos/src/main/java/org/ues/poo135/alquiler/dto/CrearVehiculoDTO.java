package org.ues.poo135.alquiler.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CrearVehiculoDTO {

    @NotBlank(message = "La marca es obligatoria.")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio.")
    private String modelo;

    @NotBlank(message = "La placa es obligatoria.")
    @Pattern(regexp = "^[A-Z0-9-]{5,8}$", message = "La placa debe tener entre 5 y 8 caracteres alfanuméricos.")
    private String placa;

    @NotBlank(message = "La categoría es obligatoria.")
    private String categoria;

    @NotNull(message = "El precio por día es obligatorio.")
    @Positive(message = "El precio por día debe ser mayor a cero.")
    private Double precioPorDia;
}
