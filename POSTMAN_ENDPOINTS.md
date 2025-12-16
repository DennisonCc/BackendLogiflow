# Guía de Endpoints - BackendLogiflow

## 🌐 URLs Base

### A través de Kong Gateway (Recomendado)
```
http://localhost:8080
```

### Acceso Directo a Microservicios
- AuthService: `http://localhost:8081`
- PedidoService: `http://localhost:8082`
- BillingService: `http://localhost:8083`
- FleetService: `http://localhost:8084`

---

## 🔐 AuthService

### 1. Registrar Usuario
**POST** `/api/auth/register`

**Kong Gateway:**
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "email": "admin@logiflow.com",
  "rol": "ADMIN"
}
```

**Directo:**
```
POST http://localhost:8081/api/auth/register
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "email": "admin@logiflow.com",
  "rol": "ADMIN"
}
```

**Respuesta:**
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@logiflow.com",
  "rol": "ADMIN"
}
```

---

### 2. Login
**POST** `/api/auth/login`

**Kong Gateway:**
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Directo:**
```
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbCI6IkFETUlOIiwiaWF0IjoxNzM0Mzc...",
  "username": "admin",
  "rol": "ADMIN"
}
```

**💡 Importante:** Guarda el `token` para usarlo en las siguientes peticiones.

---

### 3. Refresh Token
**POST** `/api/auth/token/refresh`

**Kong Gateway:**
```
POST http://localhost:8080/api/auth/token/refresh
Content-Type: application/json

{
  "refreshToken": "tu-refresh-token-aqui"
}
```

**Directo:**
```
POST http://localhost:8081/api/auth/token/refresh
Content-Type: application/json

{
  "refreshToken": "tu-refresh-token-aqui"
}
```

---

## 📦 PedidoService

**⚠️ Todos los endpoints requieren autenticación:**
```
Authorization: Bearer {token}
```

### 1. Crear Pedido
**POST** `/api/pedidos`

**Kong Gateway:**
```
POST http://localhost:8080/api/pedidos
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "clienteId": 1,
  "direccionOrigen": "Av. Amazonas N24-03, Quito",
  "direccionDestino": "La Carolina, Quito",
  "tipoEntrega": "Urbana",
  "descripcionPaquete": "Documentos importantes",
  "peso": 2.5,
  "dimensiones": "30x20x10 cm"
}
```

**Directo:**
```
POST http://localhost:8082/api/pedidos
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "clienteId": 1,
  "direccionOrigen": "Av. Amazonas N24-03, Quito",
  "direccionDestino": "La Carolina, Quito",
  "tipoEntrega": "Urbana",
  "descripcionPaquete": "Documentos importantes",
  "peso": 2.5,
  "dimensiones": "30x20x10 cm"
}
```

**Tipos de Entrega disponibles:**
- `Urbana` - Dentro de la ciudad
- `Municipal` - Entre ciudades del mismo municipio
- `Interprovincial` - Entre provincias

**Respuesta:**
```json
{
  "id": 1,
  "clienteId": 1,
  "direccionOrigen": "Av. Amazonas N24-03, Quito",
  "direccionDestino": "La Carolina, Quito",
  "tipoEntrega": "Urbana",
  "descripcionPaquete": "Documentos importantes",
  "peso": 2.5,
  "dimensiones": "30x20x10 cm",
  "estado": "RECIBIDO",
  "fechaCreacion": "2025-12-16T15:30:00"
}
```

---

### 2. Obtener Pedido por ID
**GET** `/api/pedidos/{id}`

**Kong Gateway:**
```
GET http://localhost:8080/api/pedidos/1
Authorization: Bearer {tu-token}
```

**Directo:**
```
GET http://localhost:8082/api/pedidos/1
Authorization: Bearer {tu-token}
```

---

### 3. Listar Pedidos de Cliente
**GET** `/api/pedidos/cliente/{clienteId}`

**Kong Gateway:**
```
GET http://localhost:8080/api/pedidos/cliente/1
Authorization: Bearer {tu-token}
```

**Directo:**
```
GET http://localhost:8082/api/pedidos/cliente/1
Authorization: Bearer {tu-token}
```

---

### 4. Actualizar Estado del Pedido
**PATCH** `/api/pedidos/{id}/estado`

**Kong Gateway:**
```
PATCH http://localhost:8080/api/pedidos/1/estado
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "nuevoEstado": "EN_PROCESO"
}
```

**Directo:**
```
PATCH http://localhost:8082/api/pedidos/1/estado
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "nuevoEstado": "EN_PROCESO"
}
```

**Estados disponibles:**
- `RECIBIDO` - Pedido recibido
- `EN_PROCESO` - En preparación
- `ENTREGADO` - Entregado al cliente
- `CANCELADO` - Pedido cancelado

---

### 5. Cancelar Pedido
**DELETE** `/api/pedidos/{id}`

**Kong Gateway:**
```
DELETE http://localhost:8080/api/pedidos/1
Authorization: Bearer {tu-token}
```

**Directo:**
```
DELETE http://localhost:8082/api/pedidos/1
Authorization: Bearer {tu-token}
```

---

## 💰 BillingService

**⚠️ Todos los endpoints requieren autenticación:**
```
Authorization: Bearer {token}
```

### 1. Generar Factura
**POST** `/api/facturas`

**Kong Gateway:**
```
POST http://localhost:8080/api/facturas
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "pedidoId": 1,
  "clienteId": 1,
  "detalles": "Factura por envío urbano de documentos"
}
```

**Directo:**
```
POST http://localhost:8083/api/facturas
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "pedidoId": 1,
  "clienteId": 1,
  "detalles": "Factura por envío urbano de documentos"
}
```

**Respuesta:**
```json
{
  "id": 1,
  "pedidoId": 1,
  "clienteId": 1,
  "monto": 15.50,
  "estado": "BORRADOR",
  "detalles": "Factura por envío urbano de documentos",
  "fechaCreacion": "2025-12-16T15:35:00"
}
```

---

### 2. Obtener Factura por ID
**GET** `/api/facturas/{id}`

**Kong Gateway:**
```
GET http://localhost:8080/api/facturas/1
Authorization: Bearer {tu-token}
```

**Directo:**
```
GET http://localhost:8083/api/facturas/1
Authorization: Bearer {tu-token}
```

---

### 3. Obtener Factura por Pedido
**GET** `/api/facturas/pedido/{pedidoId}`

**Kong Gateway:**
```
GET http://localhost:8080/api/facturas/pedido/1
Authorization: Bearer {tu-token}
```

**Directo:**
```
GET http://localhost:8083/api/facturas/pedido/1
Authorization: Bearer {tu-token}
```

---

### 4. Listar Facturas de Cliente
**GET** `/api/facturas/cliente/{clienteId}`

**Kong Gateway:**
```
GET http://localhost:8080/api/facturas/cliente/1
Authorization: Bearer {tu-token}
```

**Directo:**
```
GET http://localhost:8083/api/facturas/cliente/1
Authorization: Bearer {tu-token}
```

---

### 5. Emitir Factura
**PATCH** `/api/facturas/{id}/emitir`

**Kong Gateway:**
```
PATCH http://localhost:8080/api/facturas/1/emitir
Authorization: Bearer {tu-token}
```

**Directo:**
```
PATCH http://localhost:8083/api/facturas/1/emitir
Authorization: Bearer {tu-token}
```

---

### 6. Marcar Factura como Pagada
**PATCH** `/api/facturas/{id}/pagar`

**Kong Gateway:**
```
PATCH http://localhost:8080/api/facturas/1/pagar
Authorization: Bearer {tu-token}
```

**Directo:**
```
PATCH http://localhost:8083/api/facturas/1/pagar
Authorization: Bearer {tu-token}
```

**Estados de Factura:**
- `BORRADOR` - Factura creada pero no emitida
- `EMITIDA` - Factura emitida al cliente
- `PAGADA` - Factura pagada
- `CANCELADA` - Factura cancelada

---

## 🚗 FleetService - Vehículos

**⚠️ Todos los endpoints requieren autenticación:**
```
Authorization: Bearer {token}
```

### 1. Crear Vehículo
**POST** `/api/fleet/vehiculos`

**Kong Gateway:**
```
POST http://localhost:8080/api/fleet/vehiculos
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "placa": "ABC-1234",
  "marca": "Toyota",
  "modelo": "Hilux",
  "capacidadCarga": 1000.0,
  "tipoVehiculo": "CAMIONETA"
}
```

**Directo:**
```
POST http://localhost:8084/api/fleet/vehiculos
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "placa": "ABC-1234",
  "marca": "Toyota",
  "modelo": "Hilux",
  "capacidadCarga": 1000.0,
  "tipoVehiculo": "CAMIONETA"
}
```

**Tipos de Vehículo:**
- `MOTOCICLETA`
- `AUTO`
- `CAMIONETA`
- `CAMION`

**Respuesta:**
```json
{
  "id": 1,
  "placa": "ABC-1234",
  "marca": "Toyota",
  "modelo": "Hilux",
  "capacidadCarga": 1000.0,
  "tipoVehiculo": "CAMIONETA",
  "estado": "DISPONIBLE"
}
```

---

### 2. Listar Todos los Vehículos
**GET** `/api/fleet/vehiculos`

**Kong Gateway:**
```
GET http://localhost:8080/api/fleet/vehiculos
Authorization: Bearer {tu-token}
```

**Directo:**
```
GET http://localhost:8084/api/fleet/vehiculos
Authorization: Bearer {tu-token}
```

---

### 3. Obtener Vehículo por ID
**GET** `/api/fleet/vehiculos/{id}`

**Kong Gateway:**
```
GET http://localhost:8080/api/fleet/vehiculos/1
Authorization: Bearer {tu-token}
```

**Directo:**
```
GET http://localhost:8084/api/fleet/vehiculos/1
Authorization: Bearer {tu-token}
```

---

### 4. Actualizar Estado del Vehículo
**PATCH** `/api/fleet/vehiculos/{id}/estado`

**Kong Gateway:**
```
PATCH http://localhost:8080/api/fleet/vehiculos/1/estado
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "nuevoEstado": "EN_RUTA"
}
```

**Directo:**
```
PATCH http://localhost:8084/api/fleet/vehiculos/1/estado
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "nuevoEstado": "EN_RUTA"
}
```

**Estados de Vehículo:**
- `DISPONIBLE` - Disponible para asignación
- `EN_RUTA` - En ruta de entrega
- `MANTENIMIENTO` - En mantenimiento

---

### 5. Eliminar Vehículo
**DELETE** `/api/fleet/vehiculos/{id}`

**Kong Gateway:**
```
DELETE http://localhost:8080/api/fleet/vehiculos/1
Authorization: Bearer {tu-token}
```

**Directo:**
```
DELETE http://localhost:8084/api/fleet/vehiculos/1
Authorization: Bearer {tu-token}
```

---

## 👤 FleetService - Repartidores

### 1. Crear Repartidor
**POST** `/api/fleet/repartidores`

**Kong Gateway:**
```
POST http://localhost:8080/api/fleet/repartidores
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "cedula": "1234567890",
  "telefono": "0991234567",
  "licenciaConducir": "B123456",
  "vehiculoId": 1
}
```

**Directo:**
```
POST http://localhost:8084/api/fleet/repartidores
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "cedula": "1234567890",
  "telefono": "0991234567",
  "licenciaConducir": "B123456",
  "vehiculoId": 1
}
```

**Respuesta:**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "cedula": "1234567890",
  "telefono": "0991234567",
  "licenciaConducir": "B123456",
  "vehiculoId": 1,
  "activo": true
}
```

---

### 2. Listar Todos los Repartidores
**GET** `/api/fleet/repartidores`

**Kong Gateway:**
```
GET http://localhost:8080/api/fleet/repartidores
Authorization: Bearer {tu-token}
```

**Directo:**
```
GET http://localhost:8084/api/fleet/repartidores
Authorization: Bearer {tu-token}
```

---

### 3. Obtener Repartidor por ID
**GET** `/api/fleet/repartidores/{id}`

**Kong Gateway:**
```
GET http://localhost:8080/api/fleet/repartidores/1
Authorization: Bearer {tu-token}
```

**Directo:**
```
GET http://localhost:8084/api/fleet/repartidores/1
Authorization: Bearer {tu-token}
```

---

### 4. Actualizar Repartidor
**PUT** `/api/fleet/repartidores/{id}`

**Kong Gateway:**
```
PUT http://localhost:8080/api/fleet/repartidores/1
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "nombre": "Juan Pérez Actualizado",
  "cedula": "1234567890",
  "telefono": "0997654321",
  "licenciaConducir": "B123456",
  "vehiculoId": 2
}
```

**Directo:**
```
PUT http://localhost:8084/api/fleet/repartidores/1
Authorization: Bearer {tu-token}
Content-Type: application/json

{
  "nombre": "Juan Pérez Actualizado",
  "cedula": "1234567890",
  "telefono": "0997654321",
  "licenciaConducir": "B123456",
  "vehiculoId": 2
}
```

---

### 5. Eliminar Repartidor
**DELETE** `/api/fleet/repartidores/{id}`

**Kong Gateway:**
```
DELETE http://localhost:8080/api/fleet/repartidores/1
Authorization: Bearer {tu-token}
```

**Directo:**
```
DELETE http://localhost:8084/api/fleet/repartidores/1
Authorization: Bearer {tu-token}
```

---

## 🧪 Flujo de Prueba Completo

### Paso 1: Registrar Usuario
```
POST http://localhost:8080/api/auth/register
Body: {"username": "cliente1", "password": "pass123", "email": "cliente1@test.com", "rol": "CLIENTE"}
```

### Paso 2: Login
```
POST http://localhost:8080/api/auth/login
Body: {"username": "cliente1", "password": "pass123"}
Guardar: token
```

### Paso 3: Crear Vehículo
```
POST http://localhost:8080/api/fleet/vehiculos
Authorization: Bearer {token}
Body: {"placa": "XYZ-789", "marca": "Chevrolet", "modelo": "D-Max", "capacidadCarga": 1200.0, "tipoVehiculo": "CAMIONETA"}
```

### Paso 4: Crear Repartidor
```
POST http://localhost:8080/api/fleet/repartidores
Authorization: Bearer {token}
Body: {"nombre": "Pedro López", "cedula": "0987654321", "telefono": "0989876543", "licenciaConducir": "C789012", "vehiculoId": 1}
```

### Paso 5: Crear Pedido
```
POST http://localhost:8080/api/pedidos
Authorization: Bearer {token}
Body: {"clienteId": 1, "direccionOrigen": "Centro Histórico, Quito", "direccionDestino": "Cumbayá, Quito", "tipoEntrega": "Urbana", "descripcionPaquete": "Paquete de prueba", "peso": 3.0, "dimensiones": "40x30x20 cm"}
```

### Paso 6: Generar Factura
```
POST http://localhost:8080/api/facturas
Authorization: Bearer {token}
Body: {"pedidoId": 1, "clienteId": 1, "detalles": "Factura de prueba"}
```

### Paso 7: Emitir Factura
```
PATCH http://localhost:8080/api/facturas/1/emitir
Authorization: Bearer {token}
```

### Paso 8: Actualizar Estado de Pedido
```
PATCH http://localhost:8080/api/pedidos/1/estado
Authorization: Bearer {token}
Body: {"nuevoEstado": "EN_PROCESO"}
```

### Paso 9: Actualizar Estado de Vehículo
```
PATCH http://localhost:8080/api/fleet/vehiculos/1/estado
Authorization: Bearer {token}
Body: {"nuevoEstado": "EN_RUTA"}
```

### Paso 10: Marcar Factura como Pagada
```
PATCH http://localhost:8080/api/facturas/1/pagar
Authorization: Bearer {token}
```

---

## 📋 Códigos de Estado HTTP

- `200 OK` - Solicitud exitosa
- `201 Created` - Recurso creado exitosamente
- `204 No Content` - Operación exitosa sin contenido de respuesta
- `400 Bad Request` - Datos de entrada inválidos
- `401 Unauthorized` - Token no válido o ausente
- `403 Forbidden` - Sin permisos para acceder al recurso
- `404 Not Found` - Recurso no encontrado
- `500 Internal Server Error` - Error del servidor

---

## 🔑 Roles Disponibles

- `ADMIN` - Acceso completo a todos los recursos
- `SUPERVISOR` - Consulta de recursos y supervisión
- `CLIENTE` - Gestión de sus propios pedidos
- `REPARTIDOR` - Actualización de estados de pedidos asignados

---

## 📝 Notas Importantes

1. **Token JWT**: Todos los endpoints (excepto login y register) requieren el token JWT en el header `Authorization: Bearer {token}`

2. **Expiración del Token**: Los tokens expiran después de 24 horas. Usa el endpoint de refresh para obtener uno nuevo.

3. **Kong Gateway vs Directo**: Se recomienda usar Kong Gateway (puerto 8080) ya que incluye:
   - Rate limiting (100 req/min)
   - CORS configurado
   - Logging centralizado
   - Punto único de entrada

4. **Validaciones**: Todos los endpoints validan los datos de entrada. Revisa los mensajes de error para más detalles.

5. **Swagger UI**: Puedes acceder a la documentación interactiva:
   - AuthService: http://localhost:8081/swagger-ui.html
   - PedidoService: http://localhost:8082/swagger-ui.html
   - BillingService: http://localhost:8083/swagger-ui.html
   - FleetService: http://localhost:8084/swagger-ui.html
