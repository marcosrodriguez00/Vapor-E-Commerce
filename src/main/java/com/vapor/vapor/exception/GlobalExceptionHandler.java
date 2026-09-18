package com.vapor.vapor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleEntidadNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(PrecioNegativoException.class)
    public ResponseEntity<String> handlePrecioNegativoException(PrecioNegativoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // ==================== NUEVOS: Autenticación y registro ====================

    // Email duplicado al registrarse
    @ExceptionHandler(EmailException.class)
    public ResponseEntity<String> handleEmailException(EmailException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // Login con un email que no existe en la base
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsuarioNoEncontrado(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }

    // Login con password incorrecto
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleCredencialesInvalidas(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }

    // Caso raro: el usuario pasó la autenticación pero no se encontró después (orElseThrow)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleElementoNoEncontrado(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado al procesar el usuario");
    }
}
