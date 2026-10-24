package com.reparaciones_luwi.backend.exception;

public class CorreoYaRegistradoException extends RuntimeException{
    public CorreoYaRegistradoException(String mensaje){
        super(mensaje);
    }
}
