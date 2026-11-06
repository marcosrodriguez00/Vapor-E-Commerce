package com.vapor.vapor.controller;

import com.vapor.vapor.model.Favorito;
import com.vapor.vapor.model.Producto;
import com.vapor.vapor.model.Usuario;
import com.vapor.vapor.repository.FavoritoRepository;
import com.vapor.vapor.repository.ProductoRepository;
import com.vapor.vapor.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    // El JwtFilter ya dejó al usuario autenticado: su "name" es el email
    private Usuario usuarioActual(Authentication auth) {
        return usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // GET /api/favoritos -> productos favoritos del usuario logueado
    @GetMapping
    public List<Producto> listar(Authentication auth) {
        Usuario usuario = usuarioActual(auth);
        return favoritoRepository.findProductosByUsuarioId(usuario.getId());
    }

    // POST /api/favoritos/{productoId} -> agrega (si ya estaba, no hace nada)
    @PostMapping("/{productoId}")
    public ResponseEntity<Void> agregar(@PathVariable Long productoId, Authentication auth) {
        Usuario usuario = usuarioActual(auth);

        if (!favoritoRepository.existsByUsuarioIdAndProductoId(usuario.getId(), productoId)) {
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

            Favorito favorito = new Favorito();
            favorito.setUsuario(usuario);
            favorito.setProducto(producto);
            favoritoRepository.save(favorito);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // DELETE /api/favoritos/{productoId} -> quita (si no estaba, no hace nada)
    @DeleteMapping("/{productoId}")
    @Transactional
    public ResponseEntity<Void> quitar(@PathVariable Long productoId, Authentication auth) {
        Usuario usuario = usuarioActual(auth);
        favoritoRepository.deleteByUsuarioIdAndProductoId(usuario.getId(), productoId);
        return ResponseEntity.noContent().build();
    }
}
