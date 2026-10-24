package com.vapor.vapor.dto;

import com.vapor.vapor.model.Orden;
import com.vapor.vapor.model.Producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenDTO(Long id, Long usuarioId, LocalDateTime fecha, BigDecimal total, List<Item> items) {

    // imagen: primera imagen del producto, o null si no tiene.
    public record Item(Long productoId, String nombre, String imagen, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {}

    public static OrdenDTO from(Orden orden) {
        List<Item> items = orden.getItems().entrySet().stream()
                .map(e -> {
                    Producto producto = e.getKey();
                    Integer cantidad = e.getValue();
                    BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
                    List<String> imagenes = producto.getImagenes();
                    String imagen = (imagenes == null || imagenes.isEmpty()) ? null : imagenes.get(0);
                    return new Item(producto.getId(), producto.getNombre(), imagen, cantidad, producto.getPrecio(), subtotal);
                })
                .toList();
        return new OrdenDTO(orden.getId(), orden.getUsuario().getId(), orden.getFecha(), orden.getTotal(), items);
    }
}
