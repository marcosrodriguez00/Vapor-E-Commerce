package com.vapor.vapor.model;

/**
 * Enum que define los roles disponibles en el sistema.
 * USER: puede comprar productos y publicar productos para vender
 * ADMIN: tiene acceso completo al sistema, puede gestionar usuarios y productos
 */
public enum Role {
    USER,   // Usuario regular que puede comprar y vender productos
    ADMIN,   // Administrador con acceso total al sistema
    VENDEDOR  // Usuario que puede publicar productos para vender
}