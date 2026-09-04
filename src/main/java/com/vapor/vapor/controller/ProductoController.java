package com.vapor.vapor.controller;

import com.vapor.vapor.dto.ProductoResponseDTO;
import com.vapor.vapor.model.Producto;
import com.vapor.vapor.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    // Inyección por constructor para consistencia con el estilo de tu equipo
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping // Endpoint: GET http://localhost:8080/api/productos
    public ResponseEntity<List<ProductoResponseDTO>> listarProductosAlfabeticamente() {
        // Recuperamos la lista ordenada directamente desde el servicio
        List<Producto> productos = productoService.findAllByOrderByNombreAsc();

        // Mapeamos cada entidad al DTO correspondiente para proteger la información
        List<ProductoResponseDTO> respuesta = productos.stream()
                .map(this::convertirADTO)
                .toList();

        // devolvemos el código de estado HTTP 200 OK junto con el JSON ordenado para que SB no lo haga 
        return ResponseEntity.ok(respuesta);
    }

    private ProductoResponseDTO convertirADTO(Producto producto) {
        // Extraemos únicamente los nombres de las categorías asociadas para simplificar el JSON
        List<String> nombresCategorias = producto.getCategorias().stream()
                .map(categoria -> categoria.getNombre())
                .toList();

        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getTipo(),
                producto.getImagenes(),
                nombresCategorias
        );
    }
}
