package com.vapor.vapor.dto;

import com.vapor.vapor.model.TipoProducto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private TipoProducto tipo;
    private List<String> imagenes;
    private List<String> categorias; // Solo transferimos los nombres de las categorías asociadas
}