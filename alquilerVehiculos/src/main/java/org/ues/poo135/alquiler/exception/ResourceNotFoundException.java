package org.ues.poo135.alquiler.exception;

/** El recurso pedido no existe. El GlobalExceptionHandler la convierte en HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
