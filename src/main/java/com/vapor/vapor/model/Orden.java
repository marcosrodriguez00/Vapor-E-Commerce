package com.vapor.vapor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemOrden> items = new ArrayList<>();

    public Orden(Long usuarioId) {
        this.usuarioId = usuarioId;
        this.fecha = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    public void agregarItem(ItemOrden item) {
        item.setOrden(this);
        item.add(item);
        recalcularTotal();
    }

    public void recalcularTotal() {
        total = items.stream()
                .map(ItemOrden::getSubtotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }
}
