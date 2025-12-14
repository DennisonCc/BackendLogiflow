# BackendLogiflow - Sistema de Logística

Sistema de microservicios para gestión logística con Spring Boot 4.0 y Spring Cloud Gateway.

## 🏗️ Arquitectura

El sistema está compuesto por los siguientes microservicios:

### 1. **API Gateway** (Puerto 8080)
- Punto de entrada único al sistema
- Enrutamiento inteligente a microservicios
- Validación JWT en rutas protegidas
- Rate limiting por IP
- Logging centralizado de requests/responses

### 2. **AuthService** (Puerto 8081)
- Autenticación y autorización
- Gestión de usuarios y roles
- Generación y validación de JWT
- Refresh tokens

**Endpoints:**
- `POST /api/auth/login` - Login de usuario
- `POST /api/auth/register` - Registro de usuario
- `POST /api/auth/token/refresh` - Renovar access token

### 3. **PedidoService** (Puerto 8082)
- Gestión completa de pedidos (CRUD)
- Validación de cobertura geográfica
- Estados: RECIBIDO, EN_PROCESO, ENTREGADO, CANCELADO
- Tipos de entrega: Urbana, Municipal, Interprovincial

**Endpoints:**
- `POST /api/pedidos` - Crear pedido
- `GET /api/pedidos/{id}` - Obtener pedido
- `GET /api/pedidos/cliente/{clienteId}` - Listar pedidos de cliente
- `PATCH /api/pedidos/{id}/estado` - Actualizar estado
- `DELETE /api/pedidos/{id}` - Cancelar pedido

### 4. **FleetService** (Puerto 8084)
- Gestión de vehículos y repartidores
- Estados de vehículo: DISPONIBLE, EN_RUTA, MANTENIMIENTO
- Asignación de vehículos a repartidores

**Endpoints Vehículos:**
- `POST /api/fleet/vehiculos` - Crear vehículo
- `GET /api/fleet/vehiculos` - Listar vehículos
- `GET /api/fleet/vehiculos/{id}` - Obtener vehículo
- `PATCH /api/fleet/vehiculos/{id}/estado` - Actualizar estado
- `DELETE /api/fleet/vehiculos/{id}` - Eliminar vehículo

**Endpoints Repartidores:**
- `POST /api/fleet/repartidores` - Crear repartidor
- `GET /api/fleet/repartidores` - Listar repartidores
- `GET /api/fleet/repartidores/{id}` - Obtener repartidor
- `PUT /api/fleet/repartidores/{id}` - Actualizar repartidor
- `DELETE /api/fleet/repartidores/{id}` - Eliminar repartidor

### 5. **BillingService** (Puerto 8083)
- Cálculo de tarifas básicas
- Generación de facturas
- Estados: BORRADOR, EMITIDA, PAGADA, CANCELADA

**Endpoints:**
- `POST /api/facturas` - Generar factura
- `GET /api/facturas/{id}` - Obtener factura
- `GET /api/facturas/pedido/{pedidoId}` - Factura por pedido
- `GET /api/facturas/cliente/{clienteId}` - Facturas por cliente
- `PATCH /api/facturas/{id}/emitir` - Emitir factura
- `PATCH /api/facturas/{id}/pagar` - Marcar como pagada

## 🚀 Requisitos

- Java 21
- Maven 3.8+
- PostgreSQL 14+ (cada servicio usa su propia base de datos)

## 📦 Configuración de Bases de Datos

Crear las siguientes bases de datos en PostgreSQL:

```sql
CREATE DATABASE authdb;
CREATE DATABASE pedidodb;
CREATE DATABASE fleetdb;
CREATE DATABASE billingdb;
```

Configurar el usuario y contraseña en cada `application.yaml` según tu instalación de PostgreSQL.

## 🔧 Instalación y Ejecución

### 1. Compilar todos los servicios

```bash
# AuthService
cd AuthService
mvn clean install

# PedidoService
cd ../PedidoService
mvn clean install

# FleetService
cd ../FleetService
mvn clean install

# BillingService
cd ../BillingService
mvn clean install

# ApiGateway
cd ../ApiGateway
mvn clean install
```

### 2. Ejecutar los servicios (en orden)

```bash
# 1. AuthService (Puerto 8081)
cd AuthService
mvn spring-boot:run

# 2. PedidoService (Puerto 8082)
cd ../PedidoService
mvn spring-boot:run

# 3. BillingService (Puerto 8083)
cd ../BillingService
mvn spring-boot:run

# 4. FleetService (Puerto 8084)
cd ../FleetService
mvn spring-boot:run

# 5. API Gateway (Puerto 8080) - Último
cd ../ApiGateway
mvn spring-boot:run
```

## 📚 Documentación API (Swagger)

Una vez iniciados los servicios, acceder a:

- **AuthService**: http://localhost:8081/swagger-ui.html
- **PedidoService**: http://localhost:8082/swagger-ui.html
- **FleetService**: http://localhost:8084/swagger-ui.html
- **BillingService**: http://localhost:8083/swagger-ui.html

## 🔐 Autenticación

### 1. Registrar un usuario

```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "email": "admin@logiflow.com",
  "rol": "ADMIN"
}
```

### 2. Login

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "rol": "ADMIN"
}
```

### 3. Usar el token en requests protegidos

```bash
GET http://localhost:8080/api/pedidos/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## ✅ Criterios de Aceptación Cumplidos

### ✅ Microservicios REST con CRUD
- **AuthService**: Login, registro, refresh token ✅
- **PedidoService**: CRUD completo + validación de cobertura ✅
- **FleetService**: Gestión de vehículos y repartidores ✅
- **BillingService**: Cálculo de tarifas y generación de facturas ✅

### ✅ API Gateway
- Enrutamiento por prefijo (/api/pedidos → PedidoService) ✅
- Validación JWT en rutas protegidas (401/403) ✅
- Rate limiting por IP ✅
- Logging centralizado (método, URI, código, userId) ✅

### ✅ Requisitos Técnicos
- Transacciones ACID con `@Transactional` ✅
- Validación de entrada con Jakarta Validation ✅
- Documentación OpenAPI 3.0 en /swagger-ui.html ✅

## 🧪 Ejemplo de Flujo Completo

```bash
# 1. Registrar usuario
POST http://localhost:8080/api/auth/register
{
  "username": "cliente1",
  "password": "pass123",
  "email": "cliente1@test.com",
  "rol": "CLIENTE"
}

# 2. Login
POST http://localhost:8080/api/auth/login
{
  "username": "cliente1",
  "password": "pass123"
}

# 3. Crear pedido urbano (usar token del paso 2)
POST http://localhost:8080/api/pedidos
Authorization: Bearer <token>
{
  "clienteId": 1,
  "direccionOrigen": "Av. Amazonas, Quito",
  "direccionDestino": "La Carolina, Quito",
  "tipoEntrega": "Urbana",
  "descripcionPaquete": "Documentos"
}

# 4. Consultar pedido
GET http://localhost:8080/api/pedidos/1
Authorization: Bearer <token>

# Respuesta muestra estado: RECIBIDO
```

## 📋 Puertos Utilizados

| Servicio | Puerto |
|----------|--------|
| API Gateway | 8080 |
| AuthService | 8081 |
| PedidoService | 8082 |
| BillingService | 8083 |
| FleetService | 8084 |

## 🔍 Troubleshooting

1. **Error de conexión a base de datos**: Verificar que PostgreSQL esté corriendo y las bases de datos estén creadas.

2. **401 Unauthorized**: Verificar que el token JWT esté incluido en el header `Authorization: Bearer <token>`.

3. **Gateway timeout**: Asegurarse de que todos los microservicios estén ejecutándose antes de iniciar el Gateway.

4. **Rate limit exceeded**: El sistema limita requests por IP. Esperar 1 minuto o ajustar la configuración en el Gateway.

## 👥 Roles Disponibles

- `ADMIN`: Acceso completo al sistema
- `SUPERVISOR`: Consulta de pedidos y supervisión
- `CLIENTE`: Gestión de sus propios pedidos
- `REPARTIDOR`: Actualización de estado de pedidos asignados

---

**Desarrollado para LogiFlow** | Fase 1: Backend - Servicios REST y API Gateway
