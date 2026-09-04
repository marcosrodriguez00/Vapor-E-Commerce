package com.vapor.vapor.service;

import com.vapor.vapor.dto.ProductoRequestDTO;
import com.vapor.vapor.dto.ProductoResponseDTO;
import com.vapor.vapor.exception.PrecioNegativoException;
import com.vapor.vapor.exception.ResourceNotFoundException;
import com.vapor.vapor.model.Producto;
import com.vapor.vapor.model.Role;
import com.vapor.vapor.model.Usuario;
import com.vapor.vapor.repository.ProductoRepository;
<<<<<<< HEAD
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
=======
import com.vapor.vapor.repository.UsuarioRepository;
>>>>>>> 7dfbd7d2bd1cf27b9ec2f1aea221c985bd19933a

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProductoService(ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

<<<<<<< HEAD
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
=======
    public List<ProductoResponseDTO> findAll() {
        return productoRepository.findAllByOrderByNombreAsc().stream()
                .map(ProductoResponseDTO::from)
                .toList();
    }

    public List<String> findGeneros() {
        return productoRepository.findDistinctGeneros();
    }

    public List<ProductoResponseDTO> findByGenero(String genero) {
        return productoRepository.findByGeneroOrderByNombreAsc(genero).stream()
                .map(ProductoResponseDTO::from)
                .toList();
    }

    public Optional<ProductoResponseDTO> findById(Long id) {
        return productoRepository.findById(id).map(ProductoResponseDTO::from);
    }

    public ProductoResponseDTO save(ProductoRequestDTO request, String emailPublicador) {
        Producto producto = new Producto();
        aplicarCambios(producto, request);

        producto.setUsuarioId(resolverUsuario(emailPublicador).getId());
        return ProductoResponseDTO.from(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponseDTO update(Long id, ProductoRequestDTO request, String emailSolicitante) {
        Producto producto = obtenerOFallar(id);
        verificarPropietario(producto, emailSolicitante);

        aplicarCambios(producto, request);
        return ProductoResponseDTO.from(productoRepository.save(producto));
    }

    @Transactional
    public void delete(Long id, String emailSolicitante) {
        Producto producto = obtenerOFallar(id);
        verificarPropietario(producto, emailSolicitante);
        productoRepository.delete(producto);
    }

    private void aplicarCambios(Producto producto, ProductoRequestDTO request) {
        validar(request);
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setGenero(request.genero());
        if (request.tipo() != null) {
            producto.setTipo(request.tipo());
        }
        producto.setImagenes(request.imagenes());
    }

    private Producto obtenerOFallar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto " + id + " no encontrado"));
    }

    /** Solo el usuario que publicó el producto (o un ADMIN) puede modificarlo o eliminarlo. */
    private void verificarPropietario(Producto producto, String emailSolicitante) {
        Usuario usuario = resolverUsuario(emailSolicitante);
        boolean esAdmin = usuario.getRole() == Role.ADMIN;
        boolean esDueño = producto.getUsuarioId() != null && producto.getUsuarioId().equals(usuario.getId());
        if (!esAdmin && !esDueño) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No podés modificar un producto que no publicaste");
        }
    }

    private Usuario resolverUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private void validar(ProductoRequestDTO request) {
        if (request.precio() != null && request.precio().compareTo(BigDecimal.ZERO) < 0) {
            throw new PrecioNegativoException("El precio no puede ser negativo");
        }
        if (request.stock() != null && request.stock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
>>>>>>> 7dfbd7d2bd1cf27b9ec2f1aea221c985bd19933a
    }
}
