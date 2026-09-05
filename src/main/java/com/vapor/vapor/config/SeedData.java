package com.vapor.vapor.config;

import com.vapor.vapor.model.Producto;
import com.vapor.vapor.model.TipoProducto;
import com.vapor.vapor.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * La base H2 es en memoria: se borra en cada reinicio, así que los datos de prueba se cargan acá.
 */
@Component
public class SeedData implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedData.class);

    private final ProductoRepository productoRepository;

    public SeedData(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        Producto hades = new Producto("Hades", "Roguelike de acción en el inframundo griego.",
                new BigDecimal("24.99"), 50, "Acción");
        hades.setImagenes(List.of("https://cdn.vapor.com/hades/portada.jpg"));

        Producto stardew = new Producto("Stardew Valley", "Simulador de granja y vida rural.",
                new BigDecimal("13.99"), 120, "Indie");
        stardew.setImagenes(List.of("https://cdn.vapor.com/stardew/portada.jpg"));

        Producto civ = new Producto("Civilization VI", "Estrategia por turnos: construí un imperio.",
                new BigDecimal("59.99"), 30, "Estrategia");

        Producto civGathering = new Producto("Civilization VI: Gathering Storm", "Expansión con clima y desastres naturales.",
                new BigDecimal("39.99"), 25, "Estrategia", TipoProducto.DLC);

        productoRepository.saveAll(List.of(hades, stardew, civ, civGathering));

        log.info("Seed cargado: {} productos", productoRepository.count());
    }
}
