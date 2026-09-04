package com.vapor.vapor.controller;

import com.vapor.vapor.dto.CategoriaResponseDTO;
import com.vapor.vapor.service.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    // Inyección por constructor: respeta el diseño libre de @Autowired que prefiere tu equipo
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        // El servicio ya se encarga de buscar las entidades y transformarlas a DTOs seguros
        List<CategoriaResponseDTO> categorias = categoriaService.obtenerTodas();
        
        // Retornamos un estado 200 OK junto con el JSON esperado por el frontend
        return ResponseEntity.ok(categorias);
    }
}