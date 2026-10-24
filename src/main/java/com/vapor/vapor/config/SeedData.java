package com.vapor.vapor.config;

import com.vapor.vapor.model.Producto;
import com.vapor.vapor.model.Role;
import com.vapor.vapor.model.TipoProducto;
import com.vapor.vapor.model.Usuario;
import com.vapor.vapor.repository.ProductoRepository;
import com.vapor.vapor.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Carga datos de prueba al arrancar la app:
 * - Usuarios de desarrollo (un ADMIN y un USER común), creados solo si no
 *   existe uno con cada email.
 * - El catálogo de productos, solo si la tabla producto está vacía.
 * Ambas verificaciones evitan duplicar datos en cada reinicio, ya que
 * la base es MySQL persistente en Docker (no H2 en memoria).
 */
@Component
public class SeedData implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedData.class);

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public SeedData(ProductoRepository productoRepository, UsuarioRepository usuarioRepository,
                    PasswordEncoder passwordEncoder) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        // Los usuarios se crean antes del guard de productos para que no dependan de él
        crearUsuarioSiNoExiste("admin@vapor.com", "Admin1234", "Admin", "Vapor", Role.ADMIN);
        crearUsuarioSiNoExiste("usuario@vapor.com", "User1234", "Usuario", "Prueba", Role.USER);

        // Guard clause: si ya hay productos cargados, no volvemos a insertar
        if (productoRepository.count() > 0) {
            log.info("Ya existen productos en la base ({}), se omite el seed.", productoRepository.count());
            return;
        }

        // ==================== Acción ====================
        Producto hades = new Producto("Hades", "Roguelike de acción en el inframundo griego.",
                new BigDecimal("24.99"), 50, "Acción");
        hades.setImagenes(List.of("/images/hades.jpg"));

        Producto hadesII = new Producto("Hades II", "La secuela: ahora jugás como Melinoë contra Cronos.",
                new BigDecimal("29.99"), 40, "Acción");
        hadesII.setImagenes(List.of("/images/hades.jpg"));

        Producto doomEternal = new Producto("DOOM Eternal", "Shooter frenético contra hordas demoníacas.",
                new BigDecimal("39.99"), 60, "Acción");
        doomEternal.setImagenes(List.of());

        Producto doomAncientGods = new Producto("DOOM Eternal: The Ancient Gods - Part One",
                "Primera parte de la expansión narrativa de DOOM Eternal.",
                new BigDecimal("19.99"), 35, "Acción", TipoProducto.DLC);
        doomAncientGods.setImagenes(List.of());

        // ==================== Indie ====================
        Producto stardew = new Producto("Stardew Valley", "Simulador de granja y vida rural.",
                new BigDecimal("13.99"), 120, "Indie");
        stardew.setImagenes(List.of("/images/stardew-valley.jpg"));

        Producto hollowKnight = new Producto("Hollow Knight", "Metroidvania en el reino en ruinas de Hallownest.",
                new BigDecimal("14.99"), 80, "Indie");
        hollowKnight.setImagenes(List.of("/images/hollow-knight.jpg"));

        Producto celeste = new Producto("Celeste", "Plataformero de precisión sobre superar tus propios límites.",
                new BigDecimal("19.99"), 90, "Indie");
        celeste.setImagenes(List.of());

        // ==================== Estrategia ====================
        Producto civ = new Producto("Civilization VI", "Estrategia por turnos: construí un imperio.",
                new BigDecimal("59.99"), 30, "Estrategia");
        civ.setImagenes(List.of());

        Producto civGathering = new Producto("Civilization VI: Gathering Storm",
                "Expansión con clima y desastres naturales.",
                new BigDecimal("39.99"), 25, "Estrategia", TipoProducto.EXPANSION_PACK);
        civGathering.setImagenes(List.of());

        Producto totalWarWarhammer = new Producto("Total War: Warhammer III",
                "Estrategia por turnos y batallas en tiempo real en el universo Warhammer.",
                new BigDecimal("59.99"), 20, "Estrategia");
        totalWarWarhammer.setImagenes(List.of());

        // ==================== RPG ====================
        Producto elden = new Producto("Elden Ring", "RPG de acción en un vasto mundo abierto creado con George R. R. Martin.",
                new BigDecimal("59.99"), 45, "RPG");
        elden.setImagenes(List.of("/images/elden-ring.jpg"));

        Producto eldenShadow = new Producto("Elden Ring: Shadow of the Erdtree",
                "Expansión que explora la Tierra Sombría, más allá del Árbol Áureo.",
                new BigDecimal("39.99"), 30, "RPG", TipoProducto.EXPANSION_PACK);
        eldenShadow.setImagenes(List.of("/images/elden-ring.jpg"));

        Producto baldursGate = new Producto("Baldur's Gate 3", "RPG por turnos basado en Dungeons & Dragons.",
                new BigDecimal("59.99"), 55, "RPG");
        baldursGate.setImagenes(List.of());

        Producto witcher3GOTY = new Producto("The Witcher 3: Wild Hunt - Edición Game of the Year",
                "Incluye el juego base y las dos expansiones: Hearts of Stone y Blood and Wine.",
                new BigDecimal("49.99"), 70, "RPG", TipoProducto.EDICION_ESPECIAL);
        witcher3GOTY.setImagenes(List.of());

        // ==================== Simulación ====================
        Producto citiesSkylines = new Producto("Cities: Skylines II", "Simulador de construcción y gestión de ciudades.",
                new BigDecimal("49.99"), 40, "Simulación");
        citiesSkylines.setImagenes(List.of());

        productoRepository.saveAll(List.of(
                hades, hadesII, doomEternal, doomAncientGods,
                stardew, hollowKnight, celeste,
                civ, civGathering, totalWarWarhammer,
                elden, eldenShadow, baldursGate, witcher3GOTY,
                citiesSkylines
        ));

        log.info("Seed cargado: {} productos", productoRepository.count());
    }

    /**
     * Crea un usuario con el rol indicado si no existe (verificado por email).
     * SOLO PARA DESARROLLO: estas credenciales son públicas y no deben
     * usarse en producción.
     */
    private void crearUsuarioSiNoExiste(String email, String password, String nombre, String apellido, Role role) {
        if (usuarioRepository.existsByEmail(email)) {
            log.info("Ya existe el usuario {} ({}), se omite su seed.", role, email);
            return;
        }

        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .sexo("Otro")
                // Nunca guardar la contraseña en texto plano
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        usuarioRepository.save(usuario);

        log.info("Seed cargado: usuario {} {}", role, email);
    }
}