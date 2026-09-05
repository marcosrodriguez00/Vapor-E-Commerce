package com.vapor.vapor.dto;

import com.vapor.vapor.model.Producto;
import com.vapor.vapor.model.TipoProducto;

import java.math.BigDecimal;
import java.util.List;

public record ProductoResponseDTO(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        String genero,
        TipoProducto tipo,
        List<String> imagenes,
        Long usuarioId) {

    public static ProductoResponseDTO from(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getGenero(),
                producto.getTipo(),
                producto.getImagenes(),
                producto.getUsuarioId());
    }
}
