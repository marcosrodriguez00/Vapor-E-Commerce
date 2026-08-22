package com.vapor.vapor.dto;

import java.math.BigDecimal;
import java.util.List;


public record CompraRequest(Long usuarioId, List<Item> items) {

    public record Item(Long juegoId, Integer cantidad, BigDecimal precioUnitario) {}
}
