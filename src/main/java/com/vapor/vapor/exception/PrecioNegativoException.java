package com.vapor.vapor.exception;

public class PrecioNegativoException extends RuntimeException{

    public PrecioNegativoException(Long id) {
        super("El precio no puede ser negativo");
    }

    // Contructor: Acepta cualquier string como msj
    public PrecioNegativoException(String msj) {
        super(msj);
    }
}