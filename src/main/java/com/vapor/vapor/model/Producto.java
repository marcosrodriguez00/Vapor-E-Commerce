package com.vapor.vapor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 2000)
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    /** Licencias digitales disponibles. */
    @Column(nullable = false)
    private Integer stock;

    /** Distingue un juego base de un DLC u otro contenido vendible. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProducto tipo = TipoProducto.JUEGO;

    /** Relación Muchos a Muchos con Categorías (Reemplaza el String temporal de género) */
    @ManyToMany
    @JoinTable(
        name = "producto_categoria",
        joinColumns = @JoinColumn(name = "producto_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias = new ArrayList<>();

    /** Sólo URLs, sin upload de binarios. */
    @ElementCollection
    @CollectionTable(name = "producto_imagen", joinColumns = @JoinColumn(name = "producto_id"))
    @Column(name = "url")
    private List<String> imagenes = new ArrayList<>();

    /** Lado inverso de la ManyToMany: usuarios que tienen este producto en su biblioteca. */
    //@ManyToMany(mappedBy = "biblioteca")
    //private List<Usuario> propietarios = new ArrayList<>();

    // Constructores adaptados
    public Producto(String nombre, String descripcion, BigDecimal precio, Integer stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }

    public Producto(String nombre, String descripcion, BigDecimal precio, Integer stock, TipoProducto tipo) {
        this(nombre, descripcion, precio, stock);
        this.tipo = tipo;
    }
}