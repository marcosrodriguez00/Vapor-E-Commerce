package com.vapor.vapor.dto;

import com.vapor.vapor.model.TipoProducto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para transferir de forma segura los datos del producto
 * Evita ciclos infinitos de serialización con la relación bidireccional de categorías [6].
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private TipoProducto tipo;
    private List<String> imagenes;
    private List<String> categorias; // Exponemos solo los nombres de las categorías asociadas
}