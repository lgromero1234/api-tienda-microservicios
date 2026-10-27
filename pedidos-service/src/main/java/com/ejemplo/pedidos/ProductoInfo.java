package com.ejemplo.pedidos;

import java.math.BigDecimal;

// DTO de respuesta del microservicio de productos; no es una tabla local.
public record ProductoInfo(Long id, String nombre, BigDecimal precio, Integer stock) {}
