package com.vapor.vapor.dto;

import com.vapor.vapor.model.TipoProducto;

import java.math.BigDecimal;
import java.util.List;

public record ProductoRequestDTO(
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        String genero,
        TipoProducto tipo,
        List<String> imagenes) {
}
