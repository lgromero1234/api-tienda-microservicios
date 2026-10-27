package com.ejemplo.productos;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductoService {
    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) { this.repository = repository; }

    public List<Producto> listar() { return repository.findAll(); }

    public Producto obtener(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public Producto crear(ProductoRequest datos) {
        Producto p = new Producto();
        asignar(p, datos);
        return repository.save(p);
    }

    public Producto actualizar(Long id, ProductoRequest datos) {
        Producto p = obtener(id);
        asignar(p, datos);
        return repository.save(p);
    }

    public void eliminar(Long id) { repository.delete(obtener(id)); }

    private void asignar(Producto p, ProductoRequest datos) {
        p.setNombre(datos.nombre());
        p.setPrecio(datos.precio());
        p.setStock(datos.stock());
    }
}
