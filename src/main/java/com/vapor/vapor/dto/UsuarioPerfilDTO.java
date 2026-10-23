package com.vapor.vapor.dto;

import com.vapor.vapor.model.Role;
import com.vapor.vapor.model.Usuario;

import java.time.LocalDate;

/** Datos públicos del perfil del usuario. Nunca incluye el password. */
public record UsuarioPerfilDTO(
        Long id,
        String nombre,
        String apellido,
        String email,
        Role role,
        LocalDate fechaNacimiento,
        String sexo) {

    public static UsuarioPerfilDTO from(Usuario usuario) {
        return new UsuarioPerfilDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getFechaNacimiento(),
                usuario.getSexo());
    }
}
