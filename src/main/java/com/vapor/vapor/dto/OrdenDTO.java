package com.vapor.vapor.dto;

import com.vapor.vapor.model.Orden;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenDTO(Long id, Long usuarioId, LocalDateTime fecha, BigDecimal total, List<Item> items) {

    public record Item(Long juegoId, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {}

    public static OrdenDTO from(Orden orden) {
        List<Item> items = orden.getItems().stream()
                .map(i -> new Item(i.getJuegoId(), i.getCantidad(), i.getPrecioUnitario(), i.getSubtotal()))
                .toList();
        return new OrdenDTO(orden.getId(), orden.getUsuarioId(), orden.getFecha(), orden.getTotal(), items);
    }
}
