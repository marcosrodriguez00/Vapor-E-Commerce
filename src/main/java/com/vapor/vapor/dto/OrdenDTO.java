package com.vapor.vapor.dto;

import com.vapor.vapor.model.Orden;
import com.vapor.vapor.model.Producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenDTO(Long id, Long usuarioId, LocalDateTime fecha, BigDecimal total, List<Item> items) {

    public record Item(Long productoId, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {}

    public static OrdenDTO from(Orden orden) {
        List<Item> items = orden.getItems().entrySet().stream()
                .map(e -> {
                    Producto producto = e.getKey();
                    Integer cantidad = e.getValue();
                    BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
                    return new Item(producto.getId(), cantidad, producto.getPrecio(), subtotal);
                })
                .toList();
        return new OrdenDTO(orden.getId(), orden.getUsuarioId(), orden.getFecha(), orden.getTotal(), items);
    }
}
