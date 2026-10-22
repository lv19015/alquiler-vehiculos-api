package org.ues.poo135.alquiler.exception;

//Excepción personalizada para cuando un recurso no se encuentra en la base de datos
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}

