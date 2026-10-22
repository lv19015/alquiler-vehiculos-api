package org.ues.poo135.alquiler.exception;

//Excepción personalizada para errores de validación
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
