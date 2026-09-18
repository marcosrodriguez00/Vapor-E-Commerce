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
 * Carga datos de prueba solo si la tabla producto está vacía.
 * Esto evita duplicar los datos en cada reinicio de la app, ya que
 * la base es MySQL persistente en Docker (no H2 en memoria).
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

        // Guard clause: si ya hay productos cargados, no volvemos a insertar
        if (productoRepository.count() > 0) {
            log.info("Ya existen productos en la base ({}), se omite el seed.", productoRepository.count());
            return;
        }

        // ==================== Acción ====================
        Producto hades = new Producto("Hades", "Roguelike de acción en el inframundo griego.",
                new BigDecimal("24.99"), 50, "Acción");
        hades.setImagenes(List.of("https://cdn.vapor.com/hades/portada.jpg"));

        Producto hadesII = new Producto("Hades II", "La secuela: ahora jugás como Melinoë contra Cronos.",
                new BigDecimal("29.99"), 40, "Acción");
        hadesII.setImagenes(List.of("https://cdn.vapor.com/hades2/portada.jpg"));

        Producto doomEternal = new Producto("DOOM Eternal", "Shooter frenético contra hordas demoníacas.",
                new BigDecimal("39.99"), 60, "Acción");
        doomEternal.setImagenes(List.of("https://cdn.vapor.com/doom-eternal/portada.jpg"));

        Producto doomAncientGods = new Producto("DOOM Eternal: The Ancient Gods - Part One",
                "Primera parte de la expansión narrativa de DOOM Eternal.",
                new BigDecimal("19.99"), 35, "Acción", TipoProducto.DLC);
        doomAncientGods.setImagenes(List.of("https://cdn.vapor.com/doom-tag1/portada.jpg"));

        // ==================== Indie ====================
        Producto stardew = new Producto("Stardew Valley", "Simulador de granja y vida rural.",
                new BigDecimal("13.99"), 120, "Indie");
        stardew.setImagenes(List.of("https://cdn.vapor.com/stardew/portada.jpg"));

        Producto hollowKnight = new Producto("Hollow Knight", "Metroidvania en el reino en ruinas de Hallownest.",
                new BigDecimal("14.99"), 80, "Indie");
        hollowKnight.setImagenes(List.of("https://cdn.vapor.com/hollow-knight/portada.jpg"));

        Producto celeste = new Producto("Celeste", "Plataformero de precisión sobre superar tus propios límites.",
                new BigDecimal("19.99"), 90, "Indie");
        celeste.setImagenes(List.of("https://cdn.vapor.com/celeste/portada.jpg"));

        // ==================== Estrategia ====================
        Producto civ = new Producto("Civilization VI", "Estrategia por turnos: construí un imperio.",
                new BigDecimal("59.99"), 30, "Estrategia");
        civ.setImagenes(List.of("https://cdn.vapor.com/civ6/portada.jpg"));

        Producto civGathering = new Producto("Civilization VI: Gathering Storm",
                "Expansión con clima y desastres naturales.",
                new BigDecimal("39.99"), 25, "Estrategia", TipoProducto.EXPANSION_PACK);
        civGathering.setImagenes(List.of("https://cdn.vapor.com/civ6-gathering/portada.jpg"));

        Producto totalWarWarhammer = new Producto("Total War: Warhammer III",
                "Estrategia por turnos y batallas en tiempo real en el universo Warhammer.",
                new BigDecimal("59.99"), 20, "Estrategia");
        totalWarWarhammer.setImagenes(List.of("https://cdn.vapor.com/totalwar3/portada.jpg"));

        // ==================== RPG ====================
        Producto elden = new Producto("Elden Ring", "RPG de acción en un vasto mundo abierto creado con George R. R. Martin.",
                new BigDecimal("59.99"), 45, "RPG");
        elden.setImagenes(List.of("https://cdn.vapor.com/elden-ring/portada.jpg"));

        Producto eldenShadow = new Producto("Elden Ring: Shadow of the Erdtree",
                "Expansión que explora la Tierra Sombría, más allá del Árbol Áureo.",
                new BigDecimal("39.99"), 30, "RPG", TipoProducto.EXPANSION_PACK);
        eldenShadow.setImagenes(List.of("https://cdn.vapor.com/elden-shadow/portada.jpg"));

        Producto baldursGate = new Producto("Baldur's Gate 3", "RPG por turnos basado en Dungeons & Dragons.",
                new BigDecimal("59.99"), 55, "RPG");
        baldursGate.setImagenes(List.of("https://cdn.vapor.com/bg3/portada.jpg"));

        Producto witcher3GOTY = new Producto("The Witcher 3: Wild Hunt - Edición Game of the Year",
                "Incluye el juego base y las dos expansiones: Hearts of Stone y Blood and Wine.",
                new BigDecimal("49.99"), 70, "RPG", TipoProducto.EDICION_ESPECIAL);
        witcher3GOTY.setImagenes(List.of("https://cdn.vapor.com/witcher3-goty/portada.jpg"));

        // ==================== Simulación ====================
        Producto citiesSkylines = new Producto("Cities: Skylines II", "Simulador de construcción y gestión de ciudades.",
                new BigDecimal("49.99"), 40, "Simulación");
        citiesSkylines.setImagenes(List.of("https://cdn.vapor.com/cities2/portada.jpg"));

        productoRepository.saveAll(List.of(
                hades, hadesII, doomEternal, doomAncientGods,
                stardew, hollowKnight, celeste,
                civ, civGathering, totalWarWarhammer,
                elden, eldenShadow, baldursGate, witcher3GOTY,
                citiesSkylines
        ));

        log.info("Seed cargado: {} productos", productoRepository.count());
    }
}