package com.vapor.vapor.dto;

import com.vapor.vapor.model.Producto;
import com.vapor.vapor.model.TipoProducto;

import java.util.List;

public record BibliotecaItemDTO(
        Long id,
        String nombre,
        String descripcion,
        String genero,
        TipoProducto tipo,
        List<String> imagenes) {

    public static BibliotecaItemDTO from(Producto producto) {
        return new BibliotecaItemDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getGenero(),
                producto.getTipo(),
                producto.getImagenes());
    }
}
