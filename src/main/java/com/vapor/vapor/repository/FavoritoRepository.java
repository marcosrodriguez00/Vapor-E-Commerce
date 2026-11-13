package com.vapor.vapor.repository;

import com.vapor.vapor.model.Favorito;
import com.vapor.vapor.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    // Devuelve directamente los productos favoritos del usuario, del más nuevo al más viejo
    @Query("select f.producto from Favorito f where f.usuario.id = :usuarioId order by f.creadoEn desc")
    List<Producto> findProductosByUsuarioId(@Param("usuarioId") Long usuarioId);

    boolean existsByUsuarioIdAndProductoId(Long usuarioId, Long productoId);

    void deleteByUsuarioIdAndProductoId(Long usuarioId, Long productoId);
}
