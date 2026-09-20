package com.vapor.vapor.service;

import com.vapor.vapor.exception.ResourceNotFoundException;
import com.vapor.vapor.model.Carrito;
import com.vapor.vapor.model.Orden;
import com.vapor.vapor.model.Producto;
import com.vapor.vapor.model.Usuario;
import com.vapor.vapor.repository.OrdenRepository;
import com.vapor.vapor.repository.ProductoRepository;
import com.vapor.vapor.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoService carritoService;

    public OrdenService(OrdenRepository ordenRepository, ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository, CarritoService carritoService) {
        this.ordenRepository = ordenRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.carritoService = carritoService;
    }

    @Transactional
    public Orden crear(Long usuarioId) {
        Carrito carrito = carritoService.obtener(usuarioId);
        if (carrito.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La compra no tiene items");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario " + usuarioId + " no encontrado"));

        Orden orden = new Orden(usuario);
        for (Map.Entry<Producto, Integer> entry : carrito.getItems().entrySet()) {
            Integer cantidad = entry.getValue();
            Long productoId = entry.getKey().getId();

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto " + productoId + " no encontrado"));

            if (producto.getStock() < cantidad) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Stock insuficiente para " + producto.getNombre() + " (disponible: " + producto.getStock() + ")");
            }

            producto.setStock(producto.getStock() - cantidad);
            productoRepository.save(producto);
            orden.agregarItem(cantidad, producto);
            usuario.getBiblioteca().add(producto);
        }

        Orden guardada = ordenRepository.save(orden);
        usuarioRepository.save(usuario);
        carritoService.vaciar(usuarioId);
        return guardada;
    }

    public List<Orden> historial(Long usuarioId) {
        return ordenRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    public Orden porId(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden " + id + " no encontrada"));
    }
}
