package com.vapor.vapor.security;

/**
 * Constantes con las expresiones de @PreAuthorize para roles del sistema.
 *
 * Objetivo: evitar hardcodear strings tipo "hasRole('ADMIN')" repetidos en
 * cada controller (riesgo de typos silenciosos, ej. 'admin' en minúscula
 * no matchea con ROLE_ADMIN y falla sin avisar). Centralizarlo acá también
 * evita que cada dev toque un archivo compartido de configuración para
 * agregar su propia regla de rol.
 *
 * Uso: @PreAuthorize(Roles.ADMIN_OR_VENDEDOR)
 */
public final class Roles {

    private Roles() {
        // clase de constantes, no instanciable
    }

    public static final String ADMIN = "hasRole('ADMIN')";
    public static final String VENDEDOR = "hasRole('VENDEDOR')";
    public static final String USER = "hasRole('USER')";
    public static final String ADMIN_OR_VENDEDOR = "hasAnyRole('ADMIN','VENDEDOR')";
}