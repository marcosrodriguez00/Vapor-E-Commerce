package com.vapor.vapor.service;

import com.vapor.vapor.exception.ResourceNotFoundException;
import com.vapor.vapor.model.Carrito;
import com.vapor.vapor.model.Producto;
import com.vapor.vapor.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Carrito carga en memoria por usuarioId y no tiene persistencia se pierda al refrescar la pagina */
@Service
public class CarritoService {

    private final ProductoRepository productoRepository;
    private final Map<Long, Carrito> carritos = new ConcurrentHashMap<>();

    public CarritoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Carrito obtener(Long usuarioId) {
        return carritos.computeIfAbsent(usuarioId, Carrito::new);
    }

    public Carrito agregarItem(Long usuarioId, Long productoId, Integer cantidad) {
        Producto producto = obtenerProducto(productoId);
        Carrito carrito = obtener(usuarioId);
        carrito.agregarItem(cantidad, producto);
        return carrito;
    }

    public Carrito quitarItem(Long usuarioId, Long productoId) {
        Producto producto = obtenerProducto(productoId);
        Carrito carrito = obtener(usuarioId);
        carrito.quitarItem(producto);
        return carrito;
    }

    public void vaciar(Long usuarioId) {
        carritos.remove(usuarioId);
    }

    private Producto obtenerProducto(Long productoId) {
        return productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto " + productoId + " no encontrado"));
    }
}
