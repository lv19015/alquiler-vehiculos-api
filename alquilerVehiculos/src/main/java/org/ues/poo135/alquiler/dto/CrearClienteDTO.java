package org.ues.poo135.alquiler.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CrearClienteDTO {

    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    private String apellido;

    @NotBlank(message = "El documento de identidad es obligatorio.")
    @Pattern(regexp = "^[A-Za-z0-9-]{5,20}$",
             message = "El documento de identidad debe tener entre 5 y 20 caracteres alfanumericos o guiones.")
    private String documentoIdentidad;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato valido.")
    private String correo;

    @NotBlank(message = "El telefono es obligatorio.")
    @Pattern(regexp = "^[0-9]{4}-?[0-9]{4}$",
             message = "El telefono debe tener 8 digitos (ejemplo: 7777-1234).")
    private String telefono;

    @NotBlank(message = "La licencia de conducir es obligatoria.")
    private String licenciaConducir;
}
