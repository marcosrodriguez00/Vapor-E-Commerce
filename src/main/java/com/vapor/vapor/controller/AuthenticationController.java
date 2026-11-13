package com.vapor.vapor.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vapor.vapor.dto.LoginRequest;
import com.vapor.vapor.dto.RegisterRequest;
import com.vapor.vapor.dto.UsuarioPerfilDTO;
import com.vapor.vapor.security.JwtFilter;
import com.vapor.vapor.service.AuthenticationService;
import com.vapor.vapor.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
//anotación de Lombok que genera automáticamente un constructor que incluye todos los campos marcados como final, es igual que usar @autowired
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UsuarioService usuarioService;

    // Duración del token en ms: la cookie vence al mismo tiempo que el JWT que guarda
    @Value("${jwt.expiration}")
    private Long expiration;

    //http://localhost:8080/api/auth/register con metodo post http, enviar un body -> crear un usuario
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        //request tiene los datos del usuario a registrar, como nombre, email y contraseña
        return ResponseEntity.ok(authenticationService.register(request));
    }

    //http://localhost:8080/api/auth/login con metodo post http, enviar un body -> loguear un usuario
    // El JWT ya no viaja en el body: se manda en una cookie HttpOnly que JavaScript no puede leer
    // (un script inyectado por XSS no puede robar el token). El navegador la guarda y la reenvía sola.
    // El body devuelve el perfil para que el front sepa quién inició sesión y con qué rol.
    @PostMapping("/login")
    public ResponseEntity<UsuarioPerfilDTO> login(@RequestBody LoginRequest request) {
        String token = authenticationService.authenticate(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieToken(token, Duration.ofMillis(expiration)).toString())
                .body(usuarioService.perfil(request.getEmail()));
    }

    //http://localhost:8080/api/auth/logout con metodo post http -> cierra la sesión
    // Como el front no puede leer ni borrar una cookie HttpOnly, la borra el backend:
    // manda la misma cookie vacía con duración 0 y el navegador la descarta.
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookieToken("", Duration.ZERO).toString())
                .build();
    }

    private ResponseCookie cookieToken(String valor, Duration duracion) {
        return ResponseCookie.from(JwtFilter.COOKIE_TOKEN, valor)
                .httpOnly(true)      // inaccesible desde JavaScript (document.cookie)
                .sameSite("Strict")  // no se envía en pedidos que vienen de otro sitio: protege contra CSRF
                .path("/")           // se envía a todos los endpoints de la API
                .maxAge(duracion)
                // .secure(true) en producción con HTTPS; en localhost se trabaja con HTTP
                .build();
    }
}
