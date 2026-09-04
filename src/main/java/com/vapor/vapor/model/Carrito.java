package com.vapor.vapor.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Carrito {

    private Long id;

    private Long usuarioId;

    private LocalDateTime fecha;

    /** Producto -> cantidad. Sin precio congelado: el carrito refleja el precio vigente del producto. */
    private Map<Producto, Integer> items = new HashMap<>();

    public Carrito(Long usuarioId) {
        this.usuarioId = usuarioId;
        this.fecha = LocalDateTime.now();
    }

    public void agregarItem(Integer cantidad, Producto producto) {
        items.merge(producto, cantidad, Integer::sum);
    }

    public void quitarItem(Producto producto) {
        items.remove(producto);
    }

    public BigDecimal getSubtotal() {
        return items.entrySet().stream()
                .map(e -> e.getKey().getPrecio().multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getProductoId() {
        return items.keySet().stream().findFirst().map(Producto::getId).orElse(null);
    }

    public Integer getCantidad() {
        return items.values().stream().reduce(0, Integer::sum);
    }

    public BigDecimal getPrecioUnitario() {
        return items.keySet().stream().findFirst().map(Producto::getPrecio).orElse(null);
    }

    public HashMap<Producto, Integer> getItems() {
        return new HashMap<>(items);
    }
}
