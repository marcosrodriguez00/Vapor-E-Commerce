package com.vapor.vapor.repository;

import com.vapor.vapor.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    List<Orden> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
}
