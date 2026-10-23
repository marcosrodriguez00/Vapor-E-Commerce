package com.vapor.vapor.config;

import com.vapor.vapor.model.Producto;
import com.vapor.vapor.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga el catálogo de juegos de prueba, agregando solo los que todavía no
 * existen (comparando por nombre). No toca productos ya existentes, para no
 * romper pedidos/biblioteca ya vinculados ni pisar carga manual de un admin.
 * Para sumar un juego nuevo al catálogo: agregalo a la lista de abajo y el
 * resto del equipo lo va a recibir solo con reiniciar el backend.
 */
@Component
@Order(2)
public class SeedData implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedData.class);

    private final ProductoRepository productoRepository;

    public SeedData(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        // Mismo catálogo que src/data/products.json en el front, para que las
        // imágenes locales (/images/...) ya vinculadas en el front coincidan.
        Producto zelda = new Producto("The Legend of Zelda: Breath of the Wild",
                "Explora un vasto mundo abierto lleno de misterios y aventuras.",
                new BigDecimal("59.99"), 50, "Aventura");
        zelda.setImagenes(List.of("/images/zelda-botw.jpg"));

        Producto godOfWar = new Producto("God of War Ragnarök",
                "Kratos y Atreus enfrentan el fin de los tiempos en la mitología nórdica.",
                new BigDecimal("49.99"), 50, "Acción");
        godOfWar.setImagenes(List.of("/images/god-of-war.jpg"));

        Producto elden = new Producto("Elden Ring",
                "Un RPG de acción en un reino en ruinas gobernado por semidioses.",
                new BigDecimal("39.99"), 50, "RPG");
        elden.setImagenes(List.of("/images/elden-ring.jpg"));

        Producto hollowKnight = new Producto("Hollow Knight",
                "Aventura de acción en un reino subterráneo en ruinas.",
                new BigDecimal("14.99"), 50, "Indie");
        hollowKnight.setImagenes(List.of("/images/hollow-knight.jpg"));

        Producto stardew = new Producto("Stardew Valley",
                "Hereda una vieja granja y construye la vida que siempre soñaste.",
                new BigDecimal("9.99"), 50, "Indie");
        stardew.setImagenes(List.of("/images/stardew-valley.jpg"));

        Producto hades = new Producto("Hades",
                "Escapa del inframundo griego en este roguelike de acción.",
                new BigDecimal("24.99"), 50, "Acción");
        hades.setImagenes(List.of("/images/hades.jpg"));

        // ==================== Resto del catálogo ====================
        // Ampliación para tener más inventario con el que trabajar (admin,
        // carrito, biblioteca, etc). En vez de guardar una copia del archivo,
        // enlazan (hotlink) la carátula vertical oficial alojada en el CDN
        // de Steam (library_600x900.jpg, proporción 2:3), que es la misma
        // que usa la card de producto (.product-image { aspect-ratio: 2/3 }).
        // App IDs verificados contra la store de Steam.

        // ---- Acción ----
        Producto doomEternal = conImagenSteam(new Producto("DOOM Eternal",
                "Shooter frenético contra hordas demoníacas.",
                new BigDecimal("39.99"), 50, "Acción"), 782330);

        Producto sekiro = conImagenSteam(new Producto("Sekiro: Shadows Die Twice",
                "Un shinobi busca venganza en el Japón de finales del siglo XVI.",
                new BigDecimal("59.99"), 50, "Acción"), 814380);

        Producto re4 = conImagenSteam(new Producto("Resident Evil 4",
                "Leon S. Kennedy rescata a la hija del presidente de una secta rural.",
                new BigDecimal("49.99"), 50, "Acción"), 2050650);

        // ---- RPG ----
        Producto baldursGate = conImagenSteam(new Producto("Baldur's Gate 3",
                "RPG por turnos basado en Dungeons & Dragons.",
                new BigDecimal("59.99"), 50, "RPG"), 1086940);

        Producto witcher3 = conImagenSteam(new Producto("The Witcher 3: Wild Hunt",
                "Geralt de Rivia busca a su hija adoptiva en un vasto mundo de fantasía.",
                new BigDecimal("39.99"), 50, "RPG"), 292030);

        Producto persona5 = conImagenSteam(new Producto("Persona 5 Royal",
                "Un grupo de estudiantes se convierte en ladrones fantasma para robar corazones corruptos.",
                new BigDecimal("49.99"), 50, "RPG"), 1687950);

        Producto ff7remake = conImagenSteam(new Producto("Final Fantasy VII Remake",
                "Reimaginación del clásico: Cloud Strife se une a eco-terroristas contra una megacorporación.",
                new BigDecimal("59.99"), 50, "RPG"), 1462040);

        // ---- Indie ----
        Producto celeste = conImagenSteam(new Producto("Celeste",
                "Plataformero de precisión sobre superar tus propios límites.",
                new BigDecimal("19.99"), 50, "Indie"), 504230);

        Producto undertale = conImagenSteam(new Producto("Undertale",
                "Un RPG donde podés resolver cada conflicto sin matar a nadie.",
                new BigDecimal("9.99"), 50, "Indie"), 391540);

        Producto deadCells = conImagenSteam(new Producto("Dead Cells",
                "Roguevania de acción con combate frenético y mundos que cambian en cada corrida.",
                new BigDecimal("24.99"), 50, "Indie"), 588650);

        // ---- Estrategia ----
        Producto civ6 = conImagenSteam(new Producto("Civilization VI",
                "Estrategia por turnos: construí un imperio que perdure a través de los siglos.",
                new BigDecimal("59.99"), 50, "Estrategia"), 289070);

        Producto totalWarWarhammer = conImagenSteam(new Producto("Total War: Warhammer III",
                "Estrategia por turnos y batallas en tiempo real en el universo Warhammer.",
                new BigDecimal("59.99"), 50, "Estrategia"), 1142710);

        Producto aoe4 = conImagenSteam(new Producto("Age of Empires IV",
                "Estrategia en tiempo real a través de cuatro periodos de la historia.",
                new BigDecimal("39.99"), 50, "Estrategia"), 1466860);

        // ---- Simulación ----
        Producto citiesSkylines = conImagenSteam(new Producto("Cities: Skylines II",
                "Simulador de construcción y gestión de ciudades.",
                new BigDecimal("49.99"), 50, "Simulación"), 949230);

        Producto theSims4 = conImagenSteam(new Producto("The Sims 4",
                "Creá y controlá personas, construí sus casas y dirigí sus vidas.",
                new BigDecimal("19.99"), 50, "Simulación"), 1222670);

        // ---- Aventura ----
        Producto rdr2 = conImagenSteam(new Producto("Red Dead Redemption 2",
                "Arthur Morgan y la banda Van der Linde sobreviven en el ocaso del viejo oeste.",
                new BigDecimal("59.99"), 50, "Aventura"), 1174180);

        Producto horizonForbiddenWest = conImagenSteam(new Producto("Horizon Forbidden West",
                "Aloy explora tierras peligrosas pobladas por máquinas en un mundo posapocalíptico.",
                new BigDecimal("49.99"), 50, "Aventura"), 2420110);

        // ---- Terror ----
        Producto reVillage = conImagenSteam(new Producto("Resident Evil Village",
                "Ethan Winters busca a su hija en un pueblo europeo lleno de horrores.",
                new BigDecimal("39.99"), 50, "Terror"), 1196590);

        Producto deadSpace = conImagenSteam(new Producto("Dead Space",
                "Un ingeniero queda atrapado en una nave minera infestada de monstruos alienígenas.",
                new BigDecimal("49.99"), 50, "Terror"), 1693980);

        // ---- Deportes ----
        Producto fc25 = conImagenSteam(new Producto("EA Sports FC 25",
                "El simulador de fútbol más jugado, con clubes y selecciones oficiales.",
                new BigDecimal("59.99"), 50, "Deportes"), 2669320);

        Producto rocketLeague = conImagenSteam(new Producto("Rocket League",
                "Fútbol con autos a toda velocidad, en partidas online o en modo temporada.",
                new BigDecimal("0.00"), 50, "Deportes"), 252950);

        // ---- Segunda tanda: géneros nuevos (Shooter, Lucha, Carreras, Supervivencia, Puzzle) ----

        Producto portal2 = conImagenSteam(new Producto("Portal 2",
                "Resolvé rompecabezas con el cañón de portales mientras GLaDOS te pone a prueba.",
                new BigDecimal("9.99"), 50, "Puzzle"), 620);

        Producto cs2 = conImagenSteam(new Producto("Counter-Strike 2",
                "El shooter táctico competitivo más jugado, ahora sobre el motor Source 2.",
                new BigDecimal("0.00"), 50, "Shooter"), 730);

        Producto gtaV = conImagenSteam(new Producto("Grand Theft Auto V",
                "Tres criminales se cruzan en el sur de California, en un mundo abierto gigantesco.",
                new BigDecimal("29.99"), 50, "Acción"), 271590);

        Producto terraria = conImagenSteam(new Producto("Terraria",
                "Sandbox 2D de exploración, construcción y combate en mundos generados al azar.",
                new BigDecimal("9.99"), 50, "Indie"), 105600);

        Producto stray = conImagenSteam(new Producto("Stray",
                "Un gato callejero perdido explora una ciudad cyberpunk poblada por robots.",
                new BigDecimal("29.99"), 50, "Indie"), 1332010);

        Producto itTakesTwo = conImagenSteam(new Producto("It Takes Two",
                "Una pareja en crisis queda atrapada en cuerpos de muñecos y debe cooperar para volver a la normalidad.",
                new BigDecimal("39.99"), 50, "Aventura"), 1426210);

        Producto hogwartsLegacy = conImagenSteam(new Producto("Hogwarts Legacy",
                "RPG de mundo abierto ambientado en Hogwarts a fines del siglo XIX.",
                new BigDecimal("59.99"), 50, "RPG"), 990080);

        Producto cyberpunk2077 = conImagenSteam(new Producto("Cyberpunk 2077",
                "Un mercenario busca un implante que otorga la inmortalidad en Night City.",
                new BigDecimal("59.99"), 50, "RPG"), 1091500);

        Producto monsterHunterWorld = conImagenSteam(new Producto("Monster Hunter: World",
                "Cazá monstruos gigantes en solitario o en cooperativo en un ecosistema vivo.",
                new BigDecimal("29.99"), 50, "Acción"), 582010);

        Producto streetFighter6 = conImagenSteam(new Producto("Street Fighter 6",
                "El clásico de lucha vuelve con World Tour, Fighting Ground y Battle Hub.",
                new BigDecimal("59.99"), 50, "Lucha"), 1364780);

        Producto forzaHorizon5 = conImagenSteam(new Producto("Forza Horizon 5",
                "Carreras de mundo abierto en un México vibrante y lleno de biomas distintos.",
                new BigDecimal("59.99"), 50, "Carreras"), 1551360);

        Producto amongUs = conImagenSteam(new Producto("Among Us",
                "Encontrá al impostor entre la tripulación antes de que termine con todos.",
                new BigDecimal("4.99"), 50, "Indie"), 945360);

        Producto valheim = conImagenSteam(new Producto("Valheim",
                "Supervivencia vikinga en un mundo generado proceduralmente inspirado en la mitología nórdica.",
                new BigDecimal("19.99"), 50, "Supervivencia"), 892970);

        Producto slayTheSpire = conImagenSteam(new Producto("Slay the Spire",
                "Roguelike de cartas: armá tu mazo y escalá una torre infinita de enemigos.",
                new BigDecimal("24.99"), 50, "Indie"), 646570);

        Producto discoElysium = conImagenSteam(new Producto("Disco Elysium: The Final Cut",
                "Un detective con amnesia investiga un asesinato mientras reconstruye su identidad.",
                new BigDecimal("39.99"), 50, "RPG"), 632470);

        Producto spiderManRemastered = conImagenSteam(new Producto("Marvel's Spider-Man Remastered",
                "Peter Parker combate el crimen y equilibra su vida personal en Nueva York.",
                new BigDecimal("59.99"), 50, "Acción"), 1817070);

        Producto ghostOfTsushima = conImagenSteam(new Producto("Ghost of Tsushima: Director's Cut",
                "Un samurái se convierte en el Ghost para defender Tsushima de la invasión mongola.",
                new BigDecimal("59.99"), 50, "Aventura"), 2215430);

        List<Producto> catalogo = List.of(
                zelda, godOfWar, elden, hollowKnight, stardew, hades,
                doomEternal, sekiro, re4,
                baldursGate, witcher3, persona5, ff7remake,
                celeste, undertale, deadCells,
                civ6, totalWarWarhammer, aoe4,
                citiesSkylines, theSims4,
                rdr2, horizonForbiddenWest,
                reVillage, deadSpace,
                fc25, rocketLeague,
                portal2, cs2, gtaV, terraria, stray, itTakesTwo,
                hogwartsLegacy, cyberpunk2077, monsterHunterWorld,
                streetFighter6, forzaHorizon5, amongUs, valheim,
                slayTheSpire, discoElysium, spiderManRemastered, ghostOfTsushima
        );

        int[] contador = {0, 0}; // {nuevos, actualizados}
        List<Producto> aGuardar = catalogo.stream()
                .map(definicion -> productoRepository.findByNombre(definicion.getNombre())
                        .map(existente -> {
                            contador[1]++;
                            return sincronizarCatalogo(existente, definicion);
                        })
                        .orElseGet(() -> {
                            contador[0]++;
                            return definicion;
                        }))
                .toList();

        productoRepository.saveAll(aGuardar);
        log.info("Seed: {} juego(s) nuevo(s), {} actualizado(s) ({} en total).",
                contador[0], contador[1], productoRepository.count());
    }

    /**
     * Si el juego ya existe (por nombre), sincroniza los campos "de catálogo"
     * con lo que diga el código (precio, descripción, género, tipo, imagen),
     * así un cambio acá (ej: otra imagen) se propaga solo la próxima vez que
     * cada uno reinicie el backend. Mantiene el mismo id (no rompe FKs con
     * pedidos/biblioteca) y NO toca el stock: eso es inventario real que
     * puede haber cambiado con ventas o con la carga manual de un admin.
     */
    private static Producto sincronizarCatalogo(Producto existente, Producto definicion) {
        existente.setDescripcion(definicion.getDescripcion());
        existente.setPrecio(definicion.getPrecio());
        existente.setGenero(definicion.getGenero());
        existente.setTipo(definicion.getTipo());
        // Copia mutable: Hibernate necesita poder hacer clear()/addAll() sobre
        // la colección persistente al mergear una entidad ya existente, y
        // List.of(...) es inmutable (tira UnsupportedOperationException).
        existente.setImagenes(new ArrayList<>(definicion.getImagenes()));
        return existente;
    }

    /**
     * Enlaza (hotlink) la carátula vertical oficial del juego alojada en el
     * CDN de Steam, sin guardar ninguna copia en el repo. library_600x900.jpg
     * es 2:3, la misma proporción que usa .product-image en Products.css.
     */
    private static Producto conImagenSteam(Producto producto, long steamAppId) {
        producto.setImagenes(List.of(
                "https://cdn.akamai.steamstatic.com/steam/apps/" + steamAppId + "/library_600x900.jpg"));
        return producto;
    }
}