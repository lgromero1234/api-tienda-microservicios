package com.ejemplo.pedidos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PedidoRequest(@NotNull @Min(1) Long productoId, @NotNull @Min(1) Integer cantidad) {}
