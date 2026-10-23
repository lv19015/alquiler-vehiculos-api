package org.ues.poo135.alquiler.dto;

import lombok.Data;

@Data
public class ClienteResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String dui;
    private String correo;
    private String telefono;
}
