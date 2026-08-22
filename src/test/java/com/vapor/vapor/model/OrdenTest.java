package com.vapor.vapor.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrdenTest {

    @Test
    void totalSumaLosSubtotalesDeCadaItem() {
        Orden orden = new Orden(1L);
        orden.agregarItem(new ItemOrden(10L, 2, new BigDecimal("19.99"))); // 39.98
        orden.agregarItem(new ItemOrden(11L, 1, new BigDecimal("59.50"))); // 59.50

        assertEquals(new BigDecimal("99.48"), orden.getTotal());
        assertEquals(2, orden.getItems().size());
        assertEquals(orden, orden.getItems().get(0).getOrden());
    }

    @Test
    void ordenSinItemsTieneTotalCero() {
        assertEquals(BigDecimal.ZERO, new Orden(1L).getTotal());
    }
}
