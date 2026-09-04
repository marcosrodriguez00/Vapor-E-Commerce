package com.vapor.vapor.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/compras")
public class CompraController {

    @GetMapping("/mock")
    public Map<String, Object> obtenerCompraMock() {
        return Map.of(
                "compra_id", "VAP-998877",
                "estado", "aprobada",
                "total", 120.50,
                "moneda", "USD"
        );
    }

}
