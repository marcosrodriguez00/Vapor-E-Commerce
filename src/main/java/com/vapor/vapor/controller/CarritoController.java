package com.vapor.vapor.controller;

import com.vapor.vapor.model.Carrito;
import com.vapor.vapor.model.Usuario;
import com.vapor.vapor.repository.UsuarioRepository;
import com.vapor.vapor.service.CarritoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioRepository usuarioRepository;

    public CarritoController(CarritoService carritoService, UsuarioRepository usuarioRepository) {
        this.carritoService = carritoService;
        this.usuarioRepository = usuarioRepository;
    }

    public record ItemRequest(Long productoId, Integer cantidad) {}

    public record ItemResponse(Long productoId, String nombre, BigDecimal precio, Integer cantidad, BigDecimal subtotal) {}

    public record CarritoResponse(Long usuarioId, List<ItemResponse> items, BigDecimal total) {
        public static CarritoResponse from(Carrito carrito) {
            List<ItemResponse> items = carrito.getItems().entrySet().stream()
                    .map(e -> new ItemResponse(
                            e.getKey().getId(),
                            e.getKey().getNombre(),
                            e.getKey().getPrecio(),
                            e.getValue(),
                            e.getKey().getPrecio().multiply(BigDecimal.valueOf(e.getValue()))))
                    .toList();
            return new CarritoResponse(carrito.getUsuarioId(), items, carrito.getSubtotal());
        }
    }

    @GetMapping("/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public CarritoResponse obtener(@PathVariable Long usuarioId, Authentication auth) {
        validarOwnershipOAdmin(usuarioId, auth);
        return CarritoResponse.from(carritoService.obtener(usuarioId));
    }

    @PostMapping("/{usuarioId}/items")
    @PreAuthorize("isAuthenticated()")
    public CarritoResponse agregarItem(@PathVariable Long usuarioId, @RequestBody ItemRequest request, Authentication auth) {
        validarOwnershipOAdmin(usuarioId, auth);
        return CarritoResponse.from(carritoService.agregarItem(usuarioId, request.productoId(), request.cantidad()));
    }

    @DeleteMapping("/{usuarioId}/items/{productoId}")
    @PreAuthorize("isAuthenticated()")
    public CarritoResponse quitarItem(@PathVariable Long usuarioId, @PathVariable Long productoId, Authentication auth) {
        validarOwnershipOAdmin(usuarioId, auth);
        return CarritoResponse.from(carritoService.quitarItem(usuarioId, productoId));
    }

    @DeleteMapping("/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void vaciar(@PathVariable Long usuarioId, Authentication auth) {
        validarOwnershipOAdmin(usuarioId, auth);
        carritoService.vaciar(usuarioId);
    }

    /** Misma validación de ownership que usan OrdenController y UsuarioController. */
    private void validarOwnershipOAdmin(Long usuarioId, Authentication auth) {
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (esAdmin) {
            return;
        }

        Usuario usuarioLogueado = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuario no autenticado correctamente"));

        if (!usuarioLogueado.getId().equals(usuarioId)) {
            throw new AccessDeniedException("No podés acceder al carrito de otro usuario");
        }
    }
}
