package com.vapor.vapor.repository;

import com.vapor.vapor.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findAllByOrderByNombreAsc();

    List<Producto> findByGeneroOrderByNombreAsc(String genero);
}
