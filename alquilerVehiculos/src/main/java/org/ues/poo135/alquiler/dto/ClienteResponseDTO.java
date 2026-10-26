package org.ues.poo135.alquiler.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String documentoIdentidad;
    private String correo;
    private String telefono;
    private String licenciaConducir;
}