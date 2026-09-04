package com.vapor.vapor.service;

import com.vapor.vapor.dto.CompraRequestDTO;
import com.vapor.vapor.model.Orden;
import com.vapor.vapor.model.Producto;
import com.vapor.vapor.repository.OrdenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class OrdenService {

    private final OrdenRepository ordenRepository;

    public OrdenService(OrdenRepository ordenRepository) {
        this.ordenRepository = ordenRepository;
    }

    @Transactional
    public Orden crear(CompraRequestDTO request) {
        validar(request);
        Orden orden = new Orden(request.usuarioId());
        for (Map.Entry<Producto, Integer> entry : request.carrito().getItems().entrySet()) {
            Producto producto = entry.getKey();
            Integer cantidad = entry.getValue();
            orden.agregarItem(cantidad, producto);
        }
        return ordenRepository.save(orden);
    }

    public List<Orden> historial(Long usuarioId) {
        return ordenRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    public Orden porId(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden " + id + " no encontrada"));
    }

    private void validar(CompraRequestDTO request) {
        if (request == null || request.usuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el usuarioId");
        }
        if (request.carrito() == null || request.carrito().getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La compra no tiene items");
        }
        for (Map.Entry<Producto, Integer> entry : request.carrito().getItems().entrySet()) {
            if (entry.getKey() == null || entry.getKey().getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el productoId en un item");
            }
            if (entry.getValue() == null || entry.getValue() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");
            }
            if (entry.getKey().getPrecio() == null || entry.getKey().getPrecio().compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio no puede ser negativo");
            }
        }
    }
}
