package com.vapor.vapor.controller;

import com.vapor.vapor.model.Producto;
import com.vapor.vapor.security.Roles;
import com.vapor.vapor.service.ProductoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public List<Producto> getAll() {
        return productoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Long id) {
        return productoService.findById(id)
            .map(ResponseEntity::ok)                          // 200
            .orElse(ResponseEntity.notFound().build());        // 404
    }

    // Solo ADMIN o VENDEDOR pueden crear productos (enunciado: "el usuario
    // que crea el producto podrá manejar el stock y eliminarlo").
    @PreAuthorize(Roles.ADMIN_OR_VENDEDOR)
    @PostMapping
    public ResponseEntity<Producto> create(@RequestBody Producto producto) {
        Producto guardado = productoService.save(producto);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED); // 201
    }
}