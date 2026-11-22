package org.ues.poo135.alquiler.exception;

/** Datos invalidos segun una regla de negocio (ej. fechas invertidas). HTTP 400. */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
