package com.vapor.vapor.dto;

import com.vapor.vapor.model.Carrito;

import java.math.BigDecimal;
import java.util.List;


public record CompraRequestDTO(Long usuarioId, Carrito carrito) {

    public record Item(Long productoId, Integer cantidad, BigDecimal precioUnitario) {}
}
