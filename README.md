# BackendLogiflow - Sistema de Logística

Sistema de microservicios para gestión logística con Spring Boot y Kong API Gateway.

## 🏗️ Arquitectura

El sistema está compuesto por los siguientes microservicios:

### 1. **Kong API Gateway** (Puerto 8000)
- Punto de entrada único al sistema (HTTP Proxy)
- Admin API en puerto 8001
- Kong Manager (GUI) en puerto 8002
- Enrutamiento inteligente a microservicios
- CORS configurado
- Rate limiting global (100 req/min, 1000 req/hora)
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
- Docker y Docker Compose
- PostgreSQL 14+ (manejado por Docker)

## 🔧 Instalación y Ejecución

### ⚡ Opción 1: Inicio Automático (Recomendado)

**Levantar todo el sistema con un solo comando:**

```powershell
.\start-all.ps1
```

Este script hace todo automáticamente:
- ✅ Levanta Kong y las bases de datos PostgreSQL
- ✅ Compila y levanta los 4 microservicios en Docker
- ✅ Espera a que todos estén listos
- ✅ Configura rutas y plugins en Kong

**Para detener todo:**
```powershell
docker-compose down
```

**Para ver logs en tiempo real:**
```powershell
docker-compose logs -f
```

---

### 🔧 Opción 2: Inicio Manual Paso a Paso

#### 1. Iniciar Kong API Gateway y Bases de Datos

```powershell
# Levantar y compilar todos los contenedores
docker-compose up -d --build

# Verificar que todos los contenedores estén corriendo
docker-compose ps
```

#### 2. Esperar a que los servicios estén listos (~30-60 segundos)

```powershell
# Ver logs de un servicio específico
docker logs auth-service -f
docker logs kong-gateway -f
```

#### 3. Configurar las Rutas en Kong

```powershell
# Ejecutar script de configuración de Kong
.\kong-config.ps1
```

Este script configura automáticamente:
- 4 servicios (auth, pedido, billing, fleet)
- 4 rutas correspondientes
- Plugin de CORS
- Rate limiting (100 req/min, 1000 req/hora)
- Logging de peticiones

## 🌐 Acceso a los Servicios

**Todos los servicios se acceden a través de Kong Gateway en el puerto 8000:**

- **AuthService**: `http://localhost:8000/api/auth/*`
- **PedidoService**: `http://localhost:8000/api/pedidos/*`
- **BillingService**: `http://localhost:8000/api/facturas/*`
- **FleetService**: `http://localhost:8000/api/fleet/*`

**Gestión de Kong:**
- **Kong Proxy**: `http://localhost:8000` (entrada principal)
- **Kong Admin API**: `http://localhost:8001` (gestión)
- **Kong Manager (GUI)**: `http://localhost:8002` (interfaz web)

## 📚 Documentación API (Swagger)

Los microservicios tienen Swagger en sus puertos directos (sin pasar por Kong):

- **AuthService**: http://localhost:8081/swagger-ui.html
- **PedidoService**: http://localhost:8082/swagger-ui.html
- **FleetService**: http://localhost:8084/swagger-ui.html
- **BillingService**: http://localhost:8083/swagger-ui.html

## 🔐 Autenticación

### 1. Registrar un usuario

```bash
POST http://localhost:8000/api/auth/register
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
POST http://localhost:8000/api/auth/login
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
GET http://localhost:8000/api/pedidos/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## ✅ Funcionalidades del Sistema

### ✅ Microservicios REST con CRUD
- **AuthService**: Login, registro, refresh token ✅
- **PedidoService**: CRUD completo + validación de cobertura ✅
- **FleetService**: Gestión de vehículos y repartidores ✅
- **BillingService**: Cálculo de tarifas y generación de facturas ✅

### ✅ Kong API Gateway
- Enrutamiento por prefijo (/api/pedidos → PedidoService) ✅
- CORS configurado globalmente ✅
- Rate limiting (100 req/min, 1000 req/hora) ✅
- Logging centralizado de requests/responses ✅
- Kong Manager para gestión visual ✅

### ✅ Requisitos Técnicos
- Transacciones ACID con `@Transactional` ✅
- Validación de entrada con Jakarta Validation ✅
- Documentación OpenAPI 3.0 en /swagger-ui.html ✅
- Bases de datos PostgreSQL aisladas por microservicio ✅

## 🧪 Ejemplo de Flujo Completo

```bash
# 1. Registrar usuario
POST http://localhost:8000/api/auth/register
{
  "username": "cliente1",
  "password": "pass123",
  "email": "cliente1@test.com",
  "rol": "CLIENTE"
}

# 2. Login
POST http://localhost:8000/api/auth/login
{
  "username": "cliente1",
  "password": "pass123"
}

# 3. Crear pedido urbano (usar token del paso 2)
POST http://localhost:8000/api/pedidos
Authorization: Bearer <token>
{
  "clienteId": 1,
  "direccionOrigen": "Av. Amazonas, Quito",
  "direccionDestino": "La Carolina, Quito",
  "tipoEntrega": "Urbana",
  "descripcionPaquete": "Documentos"
}

# 4. Consultar pedido
GET http://localhost:8000/api/pedidos/1
Authorization: Bearer <token>

# Respuesta muestra estado: RECIBIDO
```

## 📋 Puertos del Sistema

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| Kong Proxy | 8000 | **Entrada principal del sistema** |
| Kong Admin API | 8001 | API de administración de Kong |
| Kong Manager | 8002 | Interfaz web de gestión |
| AuthService | 8081 | Microservicio de autenticación |
| PedidoService | 8082 | Microservicio de pedidos |
| BillingService | 8083 | Microservicio de facturación |
| FleetService | 8084 | Microservicio de flota |

## 🔍 Gestión de Kong

### Ver configuración actual
```powershell
# Ver todos los servicios
curl http://localhost:8001/services

# Ver todas las rutas
curl http://localhost:8001/routes

# Ver plugins activos
curl http://localhost:8001/plugins
```

### Interfaz gráfica (Kong Manager)
Accede a `http://localhost:8002` para gestionar Kong visualmente.

## 🐛 Troubleshooting

1. **Error de conexión a base de datos**: 
   ```powershell
   docker-compose ps  # Verificar contenedores
   docker logs kong-database  # Ver logs
   ```

2. **Kong no responde**: 
   ```powershell
   docker logs kong-gateway -f
   docker-compose restart kong
   ```

3. **Los microservicios no responden a través de Kong**:
   - Verificar que los microservicios estén corriendo en los puertos correctos
   - Ejecutar nuevamente `.\kong-config.ps1` para reconfigurar rutas

4. **Rate limit exceeded**: El sistema limita a 100 requests por minuto. Ajustar en [kong-config.ps1](kong-config.ps1) si es necesario.

## 📚 Documentación Adicional

- [KONG_SETUP.md](KONG_SETUP.md) - Guía detallada de configuración de Kong
- Documentación de Kong: https://docs.konghq.com/

## 👥 Roles Disponibles

- `ADMIN`: Acceso completo al sistema
- `SUPERVISOR`: Consulta de pedidos y supervisión
- `CLIENTE`: Gestión de sus propios pedidos
- `REPARTIDOR`: Actualización de estado de pedidos asignados

---

**Desarrollado para LogiFlow** | Fase 1: Backend - Servicios REST y API Gateway
