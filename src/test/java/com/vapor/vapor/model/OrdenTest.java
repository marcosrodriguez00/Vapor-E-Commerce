package com.vapor.vapor.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrdenTest {

    @Test
    void totalSumaLosSubtotalesDeCadaItem() {
        Carrito carrito = new Carrito(1L);
        carrito.agregarItem(2, new Producto("CupHead 1", "desc", new BigDecimal("19.99"), 10, "Accion")); // 39.98
        carrito.agregarItem(1, new Producto("LOL", "desc", new BigDecimal("59.50"), 10, "Accion")); // 59.50

        assertEquals(new BigDecimal("99.48"), carrito.getSubtotal());
        assertEquals(2, carrito.getItems().size());
    }

    @Test
    void ordenSinItemsTieneTotalCero() {
        assertEquals(BigDecimal.ZERO, new Orden(1L).getTotal());
    }
}
