package com.vapor.vapor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    // Relación bidireccional: Lado inverso de la relación ManyToMany
    @ManyToMany(mappedBy = "categorias")
    private List<Producto> productos = new ArrayList<>();

    public Categoria(String nombre) {
        this.nombre = nombre;
    }
}