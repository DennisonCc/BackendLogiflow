# 🚀 GUÍA DE PRUEBA RÁPIDA - FASE 2

## ✅ Flujo Completo: REST → RabbitMQ → WebSocket < 2 segundos

---

## 📋 PRE-REQUISITOS

Verifica que estos servicios estén corriendo:

```powershell
# AuthService (Puerto 8081)
http://localhost:8081/actuator/health

# PedidoService (Puerto 8082)
http://localhost:8082/actuator/health

# NotificationService (Puerto 8085)
http://localhost:8085/actuator/health

# RabbitMQ (Puerto 15673)
http://localhost:15673
# Credenciales: logiflow / logiflow123
```

---

## 🎯 OPCIÓN 1: Prueba Automática (Recomendado)

### **Paso 1: Abrir Cliente WebSocket**

```powershell
cd C:\Users\USUARIO\Documents\GitHub\BackendLogiflow
start test-websocket-mejorado.html
```

### **Paso 2: En el navegador**

1. **Login:**
   - Usuario: `admin`
   - Password: `admin123`
   - Click en "🔑 Login"
   - Verificar: "✅ Login exitoso"

2. **Conectar WebSocket:**
   - Click en "🔌 Conectar"
   - Verificar: "✅ Conectado al WebSocket (autenticado)"

### **Paso 3: Ejecutar Script de Prueba**

```powershell
.\test-fase2-completo.ps1
```

### **Resultado Esperado:**

**En PowerShell:**
```
✅ AuthService
✅ PedidoService  
✅ NotificationService
✅ RabbitMQ

📋 PASO 1: Autenticación (Login)...
  ✅ Login exitoso: Usuario=admin
  🔑 Token JWT obtenido

📋 PASO 2: Crear pedido (autenticado)...
  ✅ Pedido creado: ID=1, Estado=PENDIENTE

📋 PASO 3: Actualizar a EN_RUTA (cronometrando)...
  ✅ REST completado: Estado=EN_RUTA

📋 PASO 4: Verificar publicación en RabbitMQ...
  ✅ Mensaje publicado en RabbitMQ (250ms)

╔══════════════════════════════════════════════════════════╗
║              RESULTADO DE LA PRUEBA                     ║
╚══════════════════════════════════════════════════════════╝

✅ Pedido ID: 1
✅ Estado: PENDIENTE → EN_RUTA
✅ Tiempo total: 0.85 segundos

🎉 PRUEBA EXITOSA: < 2 segundos ✅
```

**En el Navegador:**
- 🚚 **Mensaje aparece:** "Pedido #1: PENDIENTE → EN_RUTA"
- 🚀 **Alerta visual:** "¡Pedido EN RUTA!"
- ⏱️ **Latencia:** < 2000ms

---

## 🔧 OPCIÓN 2: Prueba Manual con cURL

### **1. Login y obtener token:**

```powershell
$login = @{
    nombreUsuario = "admin"
    password = "admin123"
} | ConvertTo-Json

$auth = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $login

$token = $auth.token
Write-Host "Token: $token"
```

### **2. Crear pedido:**

```powershell
$pedido = @{
    clienteId = 1
    tipoEntrega = "EXPRESS"
    direccionOrigen = "Av. Test 123"
    direccionDestino = "Calle Destino 456"
    costoEnvio = 15.50
    distanciaKm = 8.5
    tiempoEstimadoMin = 45
} | ConvertTo-Json

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$nuevoPedido = Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos" `
    -Method POST `
    -Headers $headers `
    -Body $pedido

Write-Host "Pedido creado: ID=$($nuevoPedido.id)"
```

### **3. Actualizar estado:**

```powershell
$update = @{
    nuevoEstado = "EN_RUTA"
    repartidorId = 1
} | ConvertTo-Json

$resultado = Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos/$($nuevoPedido.id)/estado" `
    -Method PUT `
    -Headers $headers `
    -Body $update

Write-Host "Estado actualizado: $($resultado.estado)"
```

### **4. Verificar en RabbitMQ:**

Abre: http://localhost:15673/#/queues/%2F/pedido.estado.actualizado

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### **Error: "Login fallido"**

```powershell
# Verificar que AuthService esté corriendo
Test-NetConnection localhost -Port 8081

# Verificar credenciales por defecto
# Usuario: admin
# Password: admin123
```

### **Error: "Token JWT inválido"**

```powershell
# El token expira. Hacer login de nuevo
# Verificar que el header Authorization sea:
# "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### **Error: "Pedido no se crea"**

```powershell
# Verificar que PedidoService esté corriendo
Test-NetConnection localhost -Port 8082

# Verificar que el token JWT se envíe en el header
```

### **WebSocket no conecta**

```powershell
# Verificar NotificationService
Test-NetConnection localhost -Port 8085

# Verificar que se hizo login primero
# El WebSocket requiere JWT en el header
```

### **No llegan notificaciones WebSocket**

1. Verificar que RabbitMQ esté corriendo (puerto 15673)
2. Verificar que NotificationService tenga logs:
   ```
   📧 [SMS/Email simulado] Pedido #1
   📤 WebSocket: Broadcast a /topic/pedido/1
   ```
3. Verificar suscripción en el navegador: "📡 Suscrito a /topic/pedidos"

---

## 📊 VERIFICACIÓN MANUAL

### **1. RabbitMQ Management UI**

```
URL: http://localhost:15673
Usuario: logiflow
Password: logiflow123

Verificar:
- Queues → pedido.estado.actualizado
- Message rates (debe haber actividad)
- Consumers: 1 (NotificationService)
```

### **2. Logs de NotificationService**

```powershell
# Ver logs en tiempo real
cd NotificationService
mvn spring-boot:run

# Buscar estas líneas:
📧 [SMS/Email simulado] Pedido #X cambió de estado
📤 WebSocket: Broadcast a /topic/pedido/X
✅ Notificación enviada
```

### **3. GraphiQL (Opcional)**

```
URL: http://localhost:8087/graphiql

Query:
query {
  pedido(id: 1) {
    id
    estado
    repartidor { nombre }
  }
}
```

---

## ✅ CRITERIO DE ACEPTACIÓN

**CUMPLE SI:**

1. ✅ Login exitoso obtiene token JWT
2. ✅ Pedido se crea con token válido
3. ✅ Actualización REST completa exitosamente
4. ✅ Mensaje aparece en cola RabbitMQ
5. ✅ NotificationService consume el mensaje
6. ✅ WebSocket recibe notificación en **< 2 segundos**
7. ✅ Cliente WebSocket autenticado con JWT

**Flujo completo:**
```
REST (PUT /estado) 
  → RabbitMQ (pedido.estado.actualizado)
    → NotificationService (@RabbitListener)
      → WebSocket (messagingTemplate.send)
        → Cliente (< 2 segundos) ✅
```

---

## 🎉 RESULTADO ESPERADO

```
Tiempo total: 0.5 - 1.5 segundos
Latencia WebSocket: < 500ms
Estado final: EN_RUTA
Notificación visual: ¡Pedido EN RUTA!
```

**¡FASE 2 COMPLETADA!** 🎊
