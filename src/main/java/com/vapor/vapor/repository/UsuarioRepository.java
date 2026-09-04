package com.vapor.vapor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vapor.vapor.model.Usuario;


/**
 * Repositorio para manejar operaciones CRUD de la entidad Usuario.
 * Create add, Read find, Update save, Delete delete.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //Optional se usa para manejar valores que pueden ser nulos de una manera más segura.
    //Si encuentra un usuario con ese email, retorna Optional.of(usuario)
    //Si no encuentra un usuario, retorna Optional.empty()

    // lo ustiliza spring security para buscar el email del usuario, y comparar la contraseña
    //automaticamente crea la consulta sql: SELECT * FROM usuario WHERE email = ?
    Optional<Usuario> findByEmail(String email);
    
    // lo utiliza Spring Security para verificar si el email ya existe antes de registrar un nuevo usuario
    //automaticamente crea la consulta sql: SELECT * FROM usuario WHERE email = ? -> true o false   
    Boolean existsByEmail(String email);
}
