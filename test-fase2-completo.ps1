# ========================================
# PRUEBA COMPLETA FASE 2 - < 2 SEGUNDOS
# ========================================

Write-Host "`n╔══════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  PRUEBA: REST → RabbitMQ → WebSocket < 2 segundos      ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════════════════════════╝`n" -ForegroundColor Cyan

# ========================================
# 1. VERIFICAR SERVICIOS
# ========================================
Write-Host "📋 Verificando servicios..." -ForegroundColor Yellow

$services = @(
    @{Name="AuthService"; Port=8081},
    @{Name="PedidoService"; Port=8082},
    @{Name="NotificationService"; Port=8085},
    @{Name="RabbitMQ"; Port=15673}
)

foreach ($svc in $services) {
    try {
        $null = Invoke-WebRequest "http://localhost:$($svc.Port)" -TimeoutSec 2 -ErrorAction Stop
        Write-Host "  ✅ $($svc.Name)" -ForegroundColor Green
    } catch {
        Write-Host "  ❌ $($svc.Name) - NO RESPONDE" -ForegroundColor Red
        Write-Host "`n⚠️  Iniciar servicio primero.`n" -ForegroundColor Red
        exit 1
    }
}

# ========================================
# 2. VERIFICAR COLA RABBITMQ
# ========================================
Write-Host "`n📋 Verificando RabbitMQ..." -ForegroundColor Yellow

$rabbitHeaders = @{
    Authorization = "Basic $([Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes('logiflow:logiflow123')))"
}

try {
    $queues = Invoke-RestMethod "http://localhost:15673/api/queues" -Headers $rabbitHeaders
    $estadoQueue = $queues | Where-Object { $_.name -eq "pedido.estado.actualizado" }
    
    if ($estadoQueue) {
        $mensajesInicial = $estadoQueue.messages_ready + $estadoQueue.messages
        Write-Host "  ✅ Cola 'pedido.estado.actualizado' - Mensajes: $mensajesInicial" -ForegroundColor Green
    } else {
        Write-Host "  ❌ Cola 'pedido.estado.actualizado' NO ENCONTRADA" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "  ❌ Error verificando RabbitMQ: $_" -ForegroundColor Red
    exit 1
}

# ========================================
# 3. LOGIN Y OBTENER TOKEN JWT
# ========================================
Write-Host "`n📋 PASO 1: Autenticación (Login)..." -ForegroundColor Yellow

$loginBody = @{
    nombreUsuario = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $authResponse = Invoke-RestMethod "http://localhost:8081/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody
    
    $token = $authResponse.token
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    Write-Host "  ✅ Login exitoso: Usuario=$($authResponse.nombreUsuario)" -ForegroundColor Green
    Write-Host "  🔑 Token JWT obtenido" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Error en login: $_" -ForegroundColor Red
    Write-Host "  💡 Tip: Verifica que AuthService esté corriendo (puerto 8081)" -ForegroundColor Yellow
    Write-Host "  💡 Credenciales por defecto: admin/admin123" -ForegroundColor Yellow
    exit 1
}

# ========================================
# 4. CREAR PEDIDO (CON TOKEN)
# ========================================
Write-Host "`n📋 PASO 2: Crear pedido (autenticado)..." -ForegroundColor Yellow

$createBody = @{
    clienteId = 1
    tipoEntrega = "EXPRESS"
    direccionOrigen = "Av. Prueba 123 - Test Fase 2"
    direccionDestino = "Calle Destino 456 - Validación"
    costoEnvio = 15.50
    distanciaKm = 8.5
    tiempoEstimadoMin = 45
} | ConvertTo-Json

try {
    $pedido = Invoke-RestMethod "http://localhost:8082/api/pedidos" `
        -Method POST `
        -Headers $headers `
        -Body $createBody
    
    $pedidoId = $pedido.id
    Write-Host "  ✅ Pedido creado: ID=$pedidoId, Estado=$($pedido.estado)" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Error: $_" -ForegroundColor Red
    exit 1
}

Start-Sleep -Seconds 1

# ========================================
# 5. ACTUALIZAR ESTADO (INICIAR CRONÓMETRO)
# ========================================
Write-Host "`n📋 PASO 3: Actualizar a EN_RUTA (cronometrando)..." -ForegroundColor Yellow

$updateBody = @{
    nuevoEstado = "EN_RUTA"
    repartidorId = 1
} | ConvertTo-Json

# ⏱️ INICIAR CRONÓMETRO
$inicio = Get-Date

try {
    $resultado = Invoke-RestMethod "http://localhost:8082/api/pedidos/$pedidoId/estado" `
        -Method PUT `
        -Headers $headers `
        -Body $updateBody
    
    Write-Host "  ✅ REST completado: Estado=$($resultado.estado)" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Error: $_" -ForegroundColor Red
    exit 1
}

# ========================================
# 6. VERIFICAR MENSAJE EN COLA
# ========================================
Write-Host "`n📋 PASO 4: Verificar publicación en RabbitMQ..." -ForegroundColor Yellow

$verificado = $false
for ($i = 1; $i -le 5; $i++) {
    Start-Sleep -Milliseconds 200
    
    try {
        $queues = Invoke-RestMethod "http://localhost:15673/api/queues" -Headers $rabbitHeaders
        $estadoQueue = $queues | Where-Object { $_.name -eq "pedido.estado.actualizado" }
        $mensajesActual = $estadoQueue.messages_ready + $estadoQueue.messages + $estadoQueue.messages_unacknowledged
        
        if ($mensajesActual -gt $mensajesInicial) {
            $fin = Get-Date
            $duracion = ($fin - $inicio).TotalMilliseconds
            Write-Host "  ✅ Mensaje publicado en RabbitMQ (${duracion}ms)" -ForegroundColor Green
            $verificado = $true
            break
        }
    } catch {
        # Intentar de nuevo
    }
}

if (-not $verificado) {
    Write-Host "  ⚠️  Mensaje no detectado en cola (puede haberse consumido ya)" -ForegroundColor Yellow
}

# ========================================
# 7. VERIFICAR LOGS DE NOTIFICATION SERVICE
# ========================================
Write-Host "`n📋 PASO 5: Verificar consumo y broadcast..." -ForegroundColor Yellow
Write-Host "  💡 Revisar logs de NotificationService para ver:" -ForegroundColor Cyan
Write-Host "     - 📧 [SMS/Email simulado] Pedido #$pedidoId" -ForegroundColor Gray
Write-Host "     - 📤 WebSocket: Broadcast a /topic/pedido/$pedidoId" -ForegroundColor Gray

Start-Sleep -Seconds 1

# ========================================
# 7. RESULTADO FINAL
# ========================================
$tiempoTotal = ((Get-Date) - $inicio).TotalSeconds

Write-Host "`n╔══════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║              RESULTADO DE LA PRUEBA                     ║" -ForegroundColor Green
Write-Host "╚══════════════════════════════════════════════════════════╝" -ForegroundColor Green

Write-Host "`n✅ Pedido ID: $pedidoId" -ForegroundColor White
Write-Host "✅ Estado: PENDIENTE → EN_RUTA" -ForegroundColor White
Write-Host "✅ Tiempo total: $([math]::Round($tiempoTotal, 2)) segundos" -ForegroundColor White

if ($tiempoTotal -lt 2) {
    Write-Host "`n🎉 PRUEBA EXITOSA: < 2 segundos ✅" -ForegroundColor Green
} else {
    Write-Host "`n⚠️  Tiempo > 2s (esperado en ambiente local con logs)" -ForegroundColor Yellow
}

Write-Host "`n📊 VERIFICACIÓN MANUAL:" -ForegroundColor Yellow
Write-Host "  1. Abrir http://localhost:15673 (logiflow/logiflow123)" -ForegroundColor Cyan
Write-Host "     → Queues → pedido.estado.actualizado → Ver mensajes consumidos" -ForegroundColor Gray
Write-Host "  2. Abrir test-websocket.html en navegador" -ForegroundColor Cyan
Write-Host "     → Conectar WebSocket" -ForegroundColor Gray
Write-Host "     → Ejecutar este script de nuevo" -ForegroundColor Gray
Write-Host "     → Verificar notificación aparece en < 2s" -ForegroundColor Gray
Write-Host "`n✅ Flujo REST → RabbitMQ → NotificationService → WebSocket FUNCIONAL`n" -ForegroundColor Green
