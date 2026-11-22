package org.ues.poo135.alquiler.exception;

/**
 * La operacion choca con el estado actual de los datos: duplicados,
 * transiciones de estado no permitidas, traslape de fechas, etc.
 * El GlobalExceptionHandler la convierte en HTTP 409.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
