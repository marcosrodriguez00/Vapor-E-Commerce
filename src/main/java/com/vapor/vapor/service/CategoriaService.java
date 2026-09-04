package com.vapor.vapor.service;

import com.vapor.vapor.dto.CategoriaResponseDTO;
import com.vapor.vapor.model.Categoria;
import com.vapor.vapor.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> obtenerTodas() {
        List<Categoria> categorias = categoriaRepository.findAll();
        return categorias.stream()
                .map(cat -> new CategoriaResponseDTO(cat.getId(), cat.getNombre()))
                .toList();
    }
}