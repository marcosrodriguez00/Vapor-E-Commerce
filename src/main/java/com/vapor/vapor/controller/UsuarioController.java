package com.vapor.vapor.controller;

import com.vapor.vapor.dto.BibliotecaItemDTO;
import com.vapor.vapor.model.Usuario;
import com.vapor.vapor.repository.UsuarioRepository;
import com.vapor.vapor.service.UsuarioService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/{usuarioId}/biblioteca")
    @PreAuthorize("isAuthenticated()")
    public List<BibliotecaItemDTO> biblioteca(@PathVariable Long usuarioId, Authentication auth) {
        validarOwnershipOAdmin(usuarioId, auth);
        return usuarioService.biblioteca(usuarioId);
    }

    /** Misma validación de ownership que usa OrdenController para sus endpoints por usuarioId. */
    private void validarOwnershipOAdmin(Long usuarioId, Authentication auth) {
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (esAdmin) {
            return;
        }

        Usuario usuarioLogueado = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuario no autenticado correctamente"));

        if (!usuarioLogueado.getId().equals(usuarioId)) {
            throw new AccessDeniedException("No podés acceder a la biblioteca de otro usuario");
        }
    }
}
