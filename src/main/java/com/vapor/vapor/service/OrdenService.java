package com.vapor.vapor.service;

import com.vapor.vapor.dto.CompraRequestDTO;
import com.vapor.vapor.model.ItemOrden;
import com.vapor.vapor.model.Orden;
import com.vapor.vapor.repository.OrdenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

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
        for (CompraRequestDTO.Item item : request.items()) {
            orden.agregarItem(new ItemOrden(item.productoId(), item.cantidad(), item.precioUnitario()));
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
        if (request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La compra no tiene items");
        }
        for (CompraRequestDTO.Item item : request.items()) {
            if (item.productoId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el productoId en un item");
            }
            if (item.cantidad() == null || item.cantidad() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");
            }
            if (item.precioUnitario() == null || item.precioUnitario().compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio no puede ser negativo");
            }
        }
    }
}
