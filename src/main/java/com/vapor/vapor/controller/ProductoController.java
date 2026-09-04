package com.vapor.vapor.controller;

<<<<<<< HEAD
import com.vapor.vapor.dto.ProductoResponseDTO;
import com.vapor.vapor.model.Producto;
=======
import com.vapor.vapor.dto.ProductoRequestDTO;
import com.vapor.vapor.dto.ProductoResponseDTO;
>>>>>>> 7dfbd7d2bd1cf27b9ec2f1aea221c985bd19933a
import com.vapor.vapor.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
<<<<<<< HEAD
=======
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
>>>>>>> 7dfbd7d2bd1cf27b9ec2f1aea221c985bd19933a
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

<<<<<<< HEAD
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
=======
    @GetMapping
    public List<ProductoResponseDTO> getAll(@RequestParam(required = false) String genero) {
        return genero == null ? productoService.findAll() : productoService.findByGenero(genero);
    }

    @GetMapping("/generos")
    public List<String> getGeneros() {
        return productoService.findGeneros();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> getById(@PathVariable Long id) {
        return productoService.findById(id)
            .map(ResponseEntity::ok)                          // 200
            .orElse(ResponseEntity.notFound().build());        // 404
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> create(@RequestBody ProductoRequestDTO producto, Authentication authentication) {
        ProductoResponseDTO guardado = productoService.save(producto, authentication.getName());
        return new ResponseEntity<>(guardado, HttpStatus.CREATED); // 201
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> update(@PathVariable Long id, @RequestBody ProductoRequestDTO producto, Authentication authentication) {
        return ResponseEntity.ok(productoService.update(id, producto, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        productoService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build(); // 204
    }
>>>>>>> 7dfbd7d2bd1cf27b9ec2f1aea221c985bd19933a
}
