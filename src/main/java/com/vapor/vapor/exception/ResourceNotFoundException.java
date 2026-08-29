package com.vapor.vapor.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(Long id) {
        super("No se encontró la entidad: " + id);
    }

    // Contructor: Acepta cualquier string como msj
    public ResourceNotFoundException(String msj) {
        super(msj);
    }
}
