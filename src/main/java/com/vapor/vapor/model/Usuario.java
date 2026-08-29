package com.vapor.vapor.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "usuarios")
/**
 * UserDetails es una interfaz de Spring Security que proporciona información básica del usuario.
 * Esta interfaz es fundamental para la autenticación y autorización en Spring Security.
 * Implementa métodos esenciales para manejar:
 * - Credenciales del usuario (username y password)
 * - Roles y permisos (authorities)
 * - Estado de la cuenta (si está bloqueada, expirada, etc.)
 */
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    @Column(unique = true)
    private String email;
    private String password;
    // El rol del usuario (ADMIN o USER) se almacena como un string en la base de datos
    // @Enumerated(EnumType.STRING) indica que el enum se guardará como texto en la base de datos, no como un número
    @Enumerated(EnumType.STRING)
    private Role role;

    // @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    // private List<Pedido> pedidos;

    /**
     * getAuthorities() devuelve la colección de roles/permisos del usuario
     * - Cada autoridad debe implementar GrantedAuthority
     * - En este caso, convertimos el enum Role a un formato "ROLE_X" (ej: ROLE_ADMIN, ROLE_USER)
     * - Spring Security utiliza estas autoridades para control de acceso y seguridad
     * - Si el rol es null, asigna por defecto "ROLE_USER"
     * ? extended GrantedAuthority cualquier clase que extienda de GrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // resultado ROLE_USER o ROLE_ADMIN
        return List.of(new SimpleGrantedAuthority("ROLE_" + (role != null ? role.name() : "USER")));
    }

    /**
     * getUsername() retorna el identificador único del usuario para autenticación
     * - En este caso usamos el email como username
     * - Spring Security utiliza este método para identificar al usuario durante
     *   el proceso de autenticación y en el contexto de seguridad
     */
    @Override
    public String getUsername() {
        return email;
    }

    //estado de la cuenta
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
