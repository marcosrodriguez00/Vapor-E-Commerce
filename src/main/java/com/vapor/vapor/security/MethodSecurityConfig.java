package com.vapor.vapor.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Habilita el soporte de @PreAuthorize / @PostAuthorize en los controllers.
 *
 * NOTA DE DISEÑO: normalmente @EnableMethodSecurity se agrega directamente
 * sobre la clase SecurityConfig. Acá se separó en su propia clase @Configuration
 * a propósito, para evitar tocar SecurityConfig.java mientras trabajamos en
 * paralelo varios devs sobre el mismo repo (ese archivo es el más propenso
 * a conflictos de merge). Funcionalmente es equivalente: Spring detecta
 * cualquier clase @Configuration al arrancar, sin importar en qué archivo esté.
 *
 * Si en el futuro se consolida el trabajo de todos, se puede fusionar esta
 * anotación de vuelta dentro de SecurityConfig sin romper nada.
 */
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
    // Habilita @PreAuthorize / @PostAuthorize en toda la app
    // sin tocar SecurityConfig.java existente.
}