package com.vapor.vapor.controller;

import com.vapor.vapor.model.Producto;
import com.vapor.vapor.security.Roles;
import com.vapor.vapor.dto.ProductoRequestDTO;
import com.vapor.vapor.dto.ProductoResponseDTO;
import com.vapor.vapor.service.ProductoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

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

    // Solo ADMIN o VENDEDOR pueden crear productos (enunciado: "el usuario
    // que crea el producto podrá manejar el stock y eliminarlo").
    @PreAuthorize(Roles.ADMIN_OR_VENDEDOR)
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
}
