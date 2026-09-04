package com.vapor.vapor.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
//este filtro se ejecuta antes de llamar al controller
//fue configurado en SecurityConfig en la instrucción `addFilterBefore`
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Este método se ejecuta en cada petición HTTP para verificar si existe un token JWT válido.
     * Dónde se utiliza:
     * - Se configura en `SecurityConfig` para que se ejecute antes que el filtro de autenticación de Spring Security.
     * - Intercepta todas las peticiones entrantes a la API.
     */
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Obtiene el encabezado "Authorization" de la petición.
        // en header se almacen esto ej: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJycGVyZXpAZ21haWwuY29tIiwicm9sZXMiOiJST0xFX1VTRVIiLCJpYXQiOjE3NjEwMDc4MzIsImV4cCI6MTc2MTA5NDIzMn0.FcLd28t-inYFaz7Sbe4slGBafJoqZtChCszmsckCLB
        String header = request.getHeader("Authorization");

        // 2. Verifica si el encabezado existe y si comienza con "Bearer ".
        if (header != null && header.startsWith("Bearer ")) {
            //extrae la parte del JWT de la cabecera de autorización, eliminando el prefijo "Bearer ".
            // ej token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJycGVyZXpAZ21haWwuY29tIiwicm9sZXMiOiJST0xFX1VTRVIiLCJpYXQiOjE3NjEwMDc4MzIsImV4cCI6MTc2MTA5NDIzMn0.FcLd28t-inYFaz7Sbe4slGBafJoqZtChCszmsckCLBU
            String token = header.substring(7);
            // 4. Valida el token usando `jwtUtil.validateToken()`.
            if (jwtUtil.validateToken(token)) {
                // 5. Si el token es válido, extrae el nombre de usuario y los roles del token.
                String username = jwtUtil.getUsername(token);
                Set<String> roles = jwtUtil.getRoles(token);

                // transformar el conjunto de roles (cadenas de texto)  en la lista de autoridades (permisos) que Spring Security necesita para verificar si el usuario tiene acceso a un recurso.
                var authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                // usuario ya autenticado 
                // crea un objeto de autenticación con los detalles del usuario y sus roles
                var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                // 8. Finalmente, pasa la petición al siguiente filtro en la cadena.
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}