# API 1: Tienda (Productos + Pedidos)

## Ejecutar

Con Docker Desktop abierto, desde esta carpeta:

```powershell
docker compose up --build -d
```

Si ves `[]` en `http://localhost:8081/api/productos` y
`http://localhost:8082/api/pedidos`, ambos servicios están respondiendo.

## Rutas CRUD

| Método | URL | Acción |
| --- | --- | --- |
| GET | `http://localhost:8081/api/productos` | Listar productos |
| GET | `http://localhost:8081/api/productos/1` | Obtener un producto |
| POST | `http://localhost:8081/api/productos` | Crear producto |
| PUT | `http://localhost:8081/api/productos/1` | Actualizar producto |
| DELETE | `http://localhost:8081/api/productos/1` | Eliminar producto |
| GET | `http://localhost:8082/api/pedidos` | Listar pedidos |
| GET | `http://localhost:8082/api/pedidos/1` | Obtener pedido |
| POST | `http://localhost:8082/api/pedidos` | Crear pedido, consultar el producto |
| PUT | `http://localhost:8082/api/pedidos/1` | Modificar pedido |
| DELETE | `http://localhost:8082/api/pedidos/1` | Eliminar pedido |

## Pasos para probar en Postman (Body > raw > JSON)

**1. Crear producto** — POST `http://localhost:8081/api/productos`:

```json
{"nombre":"Mouse","precio":45000,"stock":10}
```

**2. Crear pedido** — POST `http://localhost:8082/api/pedidos`:

```json
{"productoId":1,"cantidad":2}
```

El microservicio de pedidos consulta `http://productos:8080/api/productos/1`
dentro de la red de Docker, valida que el producto existe y que su stock alcanza,
y calcula un `total` de `90000`.

**3. Consultar** — GET `http://localhost:8082/api/pedidos`.

**4. Actualizar pedido** — PUT `http://localhost:8082/api/pedidos/1`:

```json
{"productoId":1,"cantidad":3}
```

**5. Borrar pedido** — DELETE `http://localhost:8082/api/pedidos/1`.

Los IDs dependen de los datos que ya estén guardados; ajusta el `1` si es necesario.

**Importante:** el pedido comprueba el stock, pero no descuenta las unidades del
catálogo. Es un ejemplo sencillo de comunicación HTTP entre microservicios,
no un sistema de compra con control de inventario de producción.

Para ver registros:

```powershell
docker compose logs -f
```

Para apagar: `docker compose down`. Para borrar bases de datos: `docker compose down -v`.
