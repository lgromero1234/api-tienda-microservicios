package com.ejemplo.pedidos;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoService {
    private final PedidoRepository repository;
    private final RestClient clienteProductos;

    public PedidoService(PedidoRepository repository, @Value("${productos.url}") String url) {
        this.repository = repository;
        this.clienteProductos = RestClient.builder().baseUrl(url).build();
    }

    public List<Pedido> listar() { return repository.findAll(); }
    public Pedido obtener(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }
    public Pedido crear(PedidoRequest datos) {
        Pedido p = new Pedido();
        validarYAsignar(p, datos);
        return repository.save(p);
    }
    public Pedido actualizar(Long id, PedidoRequest datos) {
        Pedido p = obtener(id);
        validarYAsignar(p, datos);
        return repository.save(p);
    }
    public void eliminar(Long id) { repository.delete(obtener(id)); }

    private void validarYAsignar(Pedido p, PedidoRequest datos) {
        ProductoInfo producto = consultarProducto(datos.productoId());
        if (producto.stock() < datos.cantidad()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente");
        }
        p.setProductoId(datos.productoId());
        p.setCantidad(datos.cantidad());
        p.setTotal(producto.precio().multiply(java.math.BigDecimal.valueOf(datos.cantidad())));
    }

    private ProductoInfo consultarProducto(Long id) {
        try {
            ProductoInfo producto = clienteProductos.get()
                .uri("/api/productos/{id}", id).retrieve().body(ProductoInfo.class);
            if (producto == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta vacia de productos");
            }
            return producto;
        } catch (RestClientResponseException error) {
            if (error.getStatusCode().value() == 404) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no existe");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error al consultar productos");
        } catch (ResourceAccessException error) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Servicio de productos no disponible");
        }
    }
}
