package com.vapor.vapor.controller;

import com.vapor.vapor.model.Carrito;
import com.vapor.vapor.service.CarritoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
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
    public CarritoResponse obtener(@PathVariable Long usuarioId) {
        return CarritoResponse.from(carritoService.obtener(usuarioId));
    }

    @PostMapping("/{usuarioId}/items")
    public CarritoResponse agregarItem(@PathVariable Long usuarioId, @RequestBody ItemRequest request) {
        return CarritoResponse.from(carritoService.agregarItem(usuarioId, request.productoId(), request.cantidad()));
    }

    @DeleteMapping("/{usuarioId}/items/{productoId}")
    public CarritoResponse quitarItem(@PathVariable Long usuarioId, @PathVariable Long productoId) {
        return CarritoResponse.from(carritoService.quitarItem(usuarioId, productoId));
    }

    @DeleteMapping("/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void vaciar(@PathVariable Long usuarioId) {
        carritoService.vaciar(usuarioId);
    }
}
