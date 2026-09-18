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

    // Guillermo: modifique metodos para que consideren la posibilidad de que la cantidad sea null, para evitar NullPointerException

    private Map<Producto, Integer> items = new HashMap<>();

    public Carrito(Long usuarioId) {
        this.usuarioId = usuarioId;
        this.fecha = LocalDateTime.now();
    }

    public void agregarItem(Integer cantidad, Producto producto) {
        if (cantidad == null) {
            cantidad = 0;
        }
        items.merge(producto, cantidad, (actual, incremento) -> actual + incremento); // Suma la cantidad si el producto ya existe en el carrito, saque la anterior por que si se agregaban 0 podia romperse
    }

    public void quitarItem(Producto producto) {
        items.remove(producto);
    }

    public BigDecimal getSubtotal() {
        return items.entrySet().stream()
                .map(e -> e.getKey().getPrecio().multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, (subtotal, importe) -> subtotal.add(importe)); // Suma los importes de cada producto multiplicado por su cantidad
    }

    public Long getProductoId() {
        Producto producto = items.keySet().stream().findFirst().orElse(null);
        return producto == null ? null : producto.getId(); // Devuelve el ID del primer producto en el carrito, o null si no hay productos
    }

    public Integer getCantidad() {
        return items.values().stream().mapToInt(cantidad -> cantidad == null ? 0 : cantidad).sum();
    }

    public BigDecimal getPrecioUnitario() {
        return items.keySet().stream().findFirst().map(producto -> producto.getPrecio()).orElse(null);
    }

    public HashMap<Producto, Integer> getItems() {
        return new HashMap<>(items);
    }
}
