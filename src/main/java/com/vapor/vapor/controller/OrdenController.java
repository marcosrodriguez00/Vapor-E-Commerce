package com.vapor.vapor.controller;

import com.vapor.vapor.dto.CompraRequestDTO;
import com.vapor.vapor.dto.OrdenDTO;
import com.vapor.vapor.model.Orden;
import com.vapor.vapor.model.Usuario;
import com.vapor.vapor.repository.UsuarioRepository;
import com.vapor.vapor.service.OrdenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;
    private final UsuarioRepository usuarioRepository;

    public OrdenController(OrdenService ordenService, UsuarioRepository usuarioRepository) {
        this.ordenService = ordenService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public OrdenDTO crear(@RequestBody CompraRequestDTO request) {
        return OrdenDTO.from(ordenService.crear(request));
    }

    @GetMapping("/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public List<OrdenDTO> historial(@PathVariable Long usuarioId, Authentication auth) {
        validarOwnershipOAdmin(usuarioId, auth);
        return ordenService.historial(usuarioId).stream().map(OrdenDTO::from).toList();
    }

    @GetMapping("/detalle/{id}")
    @PreAuthorize("isAuthenticated()")
    public OrdenDTO detalle(@PathVariable Long id, Authentication auth) {
        Orden orden = ordenService.porId(id);
        validarOwnershipOAdmin(orden.getUsuarioId(), auth);
        return OrdenDTO.from(orden);
    }

    /**
     * Valida que el usuario autenticado sea el dueño del recurso (usuarioId)
     * o tenga rol ADMIN. Si no cumple ninguna condición, lanza AccessDeniedException,
     * que Spring Security traduce automáticamente a un 403 Forbidden.
     *
     * NOTA: el JWT solo guarda el email como principal (ver JwtFilter), por eso
     * hace falta resolver el Usuario completo vía UsuarioRepository para obtener
     * su id y compararlo contra el usuarioId del recurso solicitado.
     */
    private void validarOwnershipOAdmin(Long usuarioId, Authentication auth) {
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (esAdmin) {
            return;
        }

        Usuario usuarioLogueado = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuario no autenticado correctamente"));

        if (!usuarioLogueado.getId().equals(usuarioId)) {
            throw new AccessDeniedException("No podés acceder a órdenes de otro usuario");
        }
    }
}