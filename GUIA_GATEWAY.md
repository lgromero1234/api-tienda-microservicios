# API Gateway para Tienda - Guía de instalación

## ¿Qué es?

El Gateway es el punto de entrada de los microservicios Productos y Pedidos.
En lugar de usar los puertos 8181 y 8082 en Postman, las peticiones se hacen
por el puerto **8180**. Spring Cloud Gateway reenvía la petición sin modificar
la URL, el cuerpo ni el método HTTP.

```
Postman  ──http://localhost:8180──>  gateway-service (8080 interno)
                                       ├─ /api/productos/** ──> productos:8080
                                       └─ /api/pedidos/** ────> pedidos:8080
Pedidos ──http://productos:8080──> Productos (comunicación interna directa)
```

## Instalar en un proyecto YA existente y en ejecución

1. En la carpeta de Windows que contiene actualmente tu `docker-compose.yml`,
   copia `gateway-service/`, `coleccion_postman_gateway.json` y **sustituye**
   `docker-compose.yml` por el de este ZIP.
2. El Compose conserva `name: tienda-microservicios` y los volúmenes existentes
   `productos_data` y `pedidos_data`; por eso reutiliza tus bases H2 al ejecutarse
   **en el mismo contexto Compose**.
3. Desde esa carpeta abre PowerShell y ejecuta:

   ```powershell
   docker compose up --build -d
   docker compose ps
   ```

   **No uses `docker compose down -v`: la opción `-v` borra las bases de datos.**
4. Comprobar:
   - http://localhost:8180/actuator/health → `{"status":"UP"}` (el gateway está levantado)
   - http://localhost:8180/api/productos → JSON con productos
   - http://localhost:8180/api/pedidos → JSON con pedidos
5. En Postman importa `coleccion_postman_gateway.json` y usa las solicitudes
   en orden. Al crear producto y pedido, la colección guarda automáticamente
   `productoId` y `pedidoId` para las siguientes peticiones.

## Puertos

| Servicio | Desde Windows | En red Docker |
| --- | --- | --- |
| Gateway | localhost:8180 | gateway:8080 |
| Productos | localhost:8181 | productos:8080 |
| Pedidos | localhost:8082 | pedidos:8080 |

El Gateway consulta los nombres de servicio de Docker, no `localhost:8181`
ni `localhost:8082` dentro de su contenedor.

## Solucionar problemas

```powershell
docker compose ps -a
docker compose logs gateway --tail=80
docker compose logs productos --tail=60
docker compose logs pedidos --tail=60
```

- **Puerto 8180 ocupado**: en `docker-compose.yml`, cambia exclusivamente
  `127.0.0.1:8180:8080` por `127.0.0.1:8280:8080` y usa 8280 en Postman.
- **502/503**: revisa que Productos y Pedidos estén levantados.
- **404**: verifica la ruta exacta, incluyendo `/api/productos` o `/api/pedidos`.
- **En Postman, creación de Pedido devuelve 400**: asegúrate de crear un producto
  primero y de que `productoId` coincide con el ID devuelto por Productos.

## Detalles técnicos

- Gateway: Spring Boot 3.5.15 + Spring Cloud 2025.0.3, Java 17, WebFlux.
- Los servicios originales no se modifican (Spring Boot 3.5.6).
- Para desarrollo se conservan las entradas directas por 8181 y 8082.
- El Gateway no añade autenticación ni limita solicitudes. Es un ejemplo educativo,
  no una configuración endurecida para producción.
- `/actuator/health` sólo confirma que el proceso Gateway está vivo; no comprueba
  que Productos y Pedidos respondan. Para eso ejecuta sus GET a través del Gateway.
