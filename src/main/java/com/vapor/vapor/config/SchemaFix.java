package com.vapor.vapor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Corrige columnas que hayan quedado como ENUM nativo de MySQL en bases ya
 * creadas (ddl-auto=update nunca altera el tipo de una columna existente).
 * Si se agrega un valor nuevo a un enum de Java pero la columna sigue siendo
 * un ENUM de SQL con los valores viejos, el insert explota con
 * "Data truncated for column...". Pasarla a VARCHAR evita el problema.
 * Idempotente: no hace nada si la columna ya es VARCHAR (caso normal en una
 * base nueva, donde Hibernate ya la crea como VARCHAR).
 * Corre antes que {@link SeedData} para que el seed no choque con esto.
 */
@Component
@Order(1)
public class SchemaFix implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SchemaFix.class);

    // tabla -> columna que supo guardarse como ENUM nativo
    private static final Map<String, String> COLUMNAS_A_CORREGIR = Map.of(
            "producto", "tipo",
            "usuarios", "role"
    );

    private final JdbcTemplate jdbcTemplate;

    public SchemaFix(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        COLUMNAS_A_CORREGIR.forEach(this::corregirSiEsEnum);
    }

    private void corregirSiEsEnum(String tabla, String columna) {
        String tipoActual = jdbcTemplate.queryForObject(
                "SELECT DATA_TYPE FROM information_schema.COLUMNS " +
                        "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                String.class, tabla, columna);

        if (!"enum".equalsIgnoreCase(tipoActual)) {
            return;
        }

        log.warn("Columna {}.{} todavía es ENUM nativo de una base vieja, la paso a VARCHAR(50).", tabla, columna);
        jdbcTemplate.execute("ALTER TABLE " + tabla + " MODIFY COLUMN " + columna + " VARCHAR(50) NOT NULL");
    }
}
