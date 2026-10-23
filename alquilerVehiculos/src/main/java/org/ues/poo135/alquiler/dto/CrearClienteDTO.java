package org.ues.poo135.alquiler.dto;

import lombok.Data;

@Data
public class CrearClienteDTO {
    private String nombre;
    private String apellido;
    private String dui;
    private String correo;
    private String telefono;
}