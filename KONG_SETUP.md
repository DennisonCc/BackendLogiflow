# Kong API Gateway - Guía de Configuración

## 🚀 Inicio Rápido

### 1. Iniciar Kong y las Bases de Datos

```powershell
# Levantar Kong y todas las bases de datos
docker-compose up -d

# Verificar que todos los contenedores estén corriendo
docker-compose ps
```

### 2. Configurar las Rutas en Kong

```powershell
# Ejecutar el script de configuración
.\kong-config.ps1
```

### 3. Iniciar los Microservicios

```powershell
# Terminal 1 - AuthService
cd AuthService
mvn spring-boot:run

# Terminal 2 - PedidoService
cd PedidoService
mvn spring-boot:run

# Terminal 3 - BillingService
cd BillingService
mvn spring-boot:run

# Terminal 4 - FleetService
cd FleetService
mvn spring-boot:run
```

## 📋 Puertos del Sistema

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| Kong Proxy | 8000 | Punto de entrada principal |
| Kong Admin API | 8001 | API de administración |
| Kong Manager | 8002 | Interfaz gráfica |
| AuthService | 8081 | Autenticación |
| PedidoService | 8082 | Gestión de pedidos |
| BillingService | 8083 | Facturación |
| FleetService | 8084 | Gestión de flota |

## 🔗 URLs de Acceso

Todos los servicios ahora se acceden a través de Kong en el puerto **8000**:

### AuthService
- `POST http://localhost:8000/api/auth/login`
- `POST http://localhost:8000/api/auth/register`
- `POST http://localhost:8000/api/auth/token/refresh`

### PedidoService
- `POST http://localhost:8000/api/pedidos`
- `GET http://localhost:8000/api/pedidos/{id}`
- `GET http://localhost:8000/api/pedidos/cliente/{clienteId}`
- `PATCH http://localhost:8000/api/pedidos/{id}/estado`
- `DELETE http://localhost:8000/api/pedidos/{id}`

### BillingService
- `POST http://localhost:8000/api/facturas`
- `GET http://localhost:8000/api/facturas/{id}`
- `GET http://localhost:8000/api/facturas/pedido/{pedidoId}`
- `GET http://localhost:8000/api/facturas/cliente/{clienteId}`
- `PATCH http://localhost:8000/api/facturas/{id}/emitir`
- `PATCH http://localhost:8000/api/facturas/{id}/pagar`

### FleetService
- `POST http://localhost:8000/api/fleet/vehiculos`
- `GET http://localhost:8000/api/fleet/vehiculos`
- `GET http://localhost:8000/api/fleet/vehiculos/{id}`
- `PATCH http://localhost:8000/api/fleet/vehiculos/{id}/estado`
- `DELETE http://localhost:8000/api/fleet/vehiculos/{id}`
- `POST http://localhost:8000/api/fleet/repartidores`
- `GET http://localhost:8000/api/fleet/repartidores`
- `GET http://localhost:8000/api/fleet/repartidores/{id}`
- `PUT http://localhost:8000/api/fleet/repartidores/{id}`
- `DELETE http://localhost:8000/api/fleet/repartidores/{id}`

## 🛠️ Kong Admin API

### Ver todos los servicios configurados
```powershell
curl http://localhost:8001/services
```

### Ver todas las rutas
```powershell
curl http://localhost:8001/routes
```

### Ver plugins activos
```powershell
curl http://localhost:8001/plugins
```

### Ver estadísticas de un servicio
```powershell
curl http://localhost:8001/services/auth-service
```

## 🔌 Plugins Configurados

### 1. CORS
- **Función:** Permite peticiones desde cualquier origen
- **Configuración:** 
  - Origins: `*`
  - Methods: `GET, POST, PUT, PATCH, DELETE, OPTIONS`
  - Headers: `Accept, Accept-Language, Content-Type, Authorization`

### 2. Rate Limiting
- **Función:** Limita la cantidad de peticiones
- **Configuración:**
  - 100 peticiones por minuto
  - 1000 peticiones por hora

### 3. File Log
- **Función:** Registra todas las peticiones y respuestas
- **Ubicación:** `/tmp/kong-logs.log` (dentro del contenedor)

## 📊 Kong Manager (GUI)

Accede a la interfaz gráfica de Kong en:
```
http://localhost:8002
```

Desde aquí puedes:
- Ver y gestionar servicios
- Configurar rutas
- Añadir/modificar plugins
- Monitorear el tráfico

## 🔧 Comandos Útiles

### Detener Kong y las bases de datos
```powershell
docker-compose down
```

### Ver logs de Kong
```powershell
docker logs kong-gateway -f
```

### Reiniciar Kong
```powershell
docker-compose restart kong
```

### Limpiar todo (incluyendo volúmenes)
```powershell
docker-compose down -v
```

### Reconfigurar Kong (si cambiaste algo)
```powershell
# Primero borra la configuración actual
docker exec -it kong-gateway kong config db_export /dev/null

# Luego ejecuta de nuevo el script de configuración
.\kong-config.ps1
```

## 🐛 Solución de Problemas

### Kong no inicia
```powershell
# Verificar logs de la base de datos
docker logs kong-database

# Verificar logs de Kong
docker logs kong-gateway
```

### Los servicios no responden
```powershell
# Verificar que Kong pueda conectarse a host.docker.internal
docker exec -it kong-gateway ping host.docker.internal

# En Windows, puede que necesites usar la IP de tu máquina en lugar de host.docker.internal
# Obtén tu IP con:
ipconfig

# Luego actualiza las URLs de los servicios en kong-config.ps1
```

### Error de conexión a bases de datos
```powershell
# Verifica que los contenedores de PostgreSQL estén corriendo
docker-compose ps

# Reinicia las bases de datos
docker-compose restart authdb pedidodb billingdb fleetdb
```

## 🎯 Próximos Pasos Sugeridos

1. **Autenticación JWT con Kong:**
   - Configurar el plugin JWT de Kong
   - Validar tokens en el gateway

2. **Métricas y Monitoreo:**
   - Integrar Prometheus
   - Configurar Grafana dashboards

3. **Seguridad:**
   - Configurar SSL/TLS
   - Implementar IP whitelisting
   - Configurar ACL (Access Control Lists)

4. **Escalabilidad:**
   - Configurar Kong en modo cluster
   - Implementar cache en Kong

## 📚 Recursos

- [Documentación oficial de Kong](https://docs.konghq.com/)
- [Kong Plugins Hub](https://docs.konghq.com/hub/)
- [Kong Admin API Reference](https://docs.konghq.com/gateway/latest/admin-api/)
