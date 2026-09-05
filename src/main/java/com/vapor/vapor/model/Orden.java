package com.vapor.vapor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @ElementCollection
    @CollectionTable(name = "orden_item", joinColumns = @JoinColumn(name = "orden_id"))
    @MapKeyJoinColumn(name = "producto_id")
    @Column(name = "cantidad", nullable = false)
    private Map<Producto, Integer> items = new HashMap<>();

    public Orden(Long usuarioId) {
        this.usuarioId = usuarioId;
        this.fecha = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    public void agregarItem(Integer cantidad, Producto producto) {
        items.merge(producto, cantidad, Integer::sum);
        recalcularTotal();
    }

    public void recalcularTotal() {
        total = items.entrySet().stream()
                .map(e -> e.getKey().getPrecio().multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
