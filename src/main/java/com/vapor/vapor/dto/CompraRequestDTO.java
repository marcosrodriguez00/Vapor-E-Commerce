package com.vapor.vapor.dto;

import java.math.BigDecimal;
import java.util.List;


public record CompraRequestDTO(Long usuarioId, List<Item> items) {

    public record Item(Long productoId, Integer cantidad, BigDecimal precioUnitario) {}
}
