package com.vapor.vapor.service;

import com.vapor.vapor.dto.ProductoResponseDTO;
import com.vapor.vapor.exception.ResourceNotFoundException;
import com.vapor.vapor.model.Usuario;
import com.vapor.vapor.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<ProductoResponseDTO> biblioteca(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario " + usuarioId + " no encontrado"));
        return usuario.getBiblioteca().stream().map(ProductoResponseDTO::from).toList();
    }
}
