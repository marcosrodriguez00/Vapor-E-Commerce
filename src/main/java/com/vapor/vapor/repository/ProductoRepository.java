package com.vapor.vapor.repository;

import com.vapor.vapor.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findAllByOrderByNombreAsc();

    List<Producto> findByGeneroOrderByNombreAsc(String genero);

    Optional<Producto> findByNombre(String nombre);

    @Query("select distinct p.genero from Producto p where p.genero is not null order by p.genero")
    List<String> findDistinctGeneros();
}
