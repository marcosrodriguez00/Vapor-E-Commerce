package com.vapor.vapor.service;

import com.vapor.vapor.model.Producto;
import com.vapor.vapor.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Mantengo intactos los métodos CRUD básicos para no romper nada
    @Transactional(readOnly = true) // Optimiza la consulta en MySQL
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    @Transactional // Asegura transaccionalidad para la escritura [1, 2]
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    // Agrego unicamente la consulta ordenada requerida
    // retorna la Entidad directamente
    @Transactional(readOnly = true)
    public List<Producto> findAllByOrderByNombreAsc() {
        return productoRepository.findAllByOrderByNombreAsc();
    }
}