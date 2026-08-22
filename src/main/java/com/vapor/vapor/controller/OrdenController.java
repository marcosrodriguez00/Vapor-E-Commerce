package com.vapor.vapor.controller;

import com.vapor.vapor.dto.CompraRequestDTO;
import com.vapor.vapor.dto.OrdenDTO;
import com.vapor.vapor.service.OrdenService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdenDTO crear(@RequestBody CompraRequestDTO request) {
        return OrdenDTO.from(ordenService.crear(request));
    }

    @GetMapping("/{usuarioId}")
    public List<OrdenDTO> historial(@PathVariable Long usuarioId) {
        return ordenService.historial(usuarioId).stream().map(OrdenDTO::from).toList();
    }

    @GetMapping("/detalle/{id}")
    public OrdenDTO detalle(@PathVariable Long id) {
        return OrdenDTO.from(ordenService.porId(id));
    }
}
