package com.reparaciones_luwi.backend.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CorreoYaRegistradoException.class)
        public ResponseEntity<Map<String, String>> manejarCorreoDuplicado(CorreoYaRegistradoException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> manejarEstado(ResponseStatusException ex){
        String mensaje = ex.getReason() != null? ex.getReason() : "Error en la solicitud";
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("mensaje", mensaje));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(MethodArgumentNotValidException ex){
        String mensaje =
                ex.getBindingResult().getFieldErrors().stream().map(error -> error.getDefaultMessage()).findFirst().orElse("Los datos enviados no son válidos");
        return ResponseEntity.badRequest().body(Map.of("mensaje", mensaje));
    }
}
