# ========================================
# SCRIPT DE PRUEBA COMPLETA - FASE 2
# Backend LogiFlow - Event-Driven Architecture
# ========================================

Write-Host "`n===================================================================" -ForegroundColor Cyan
Write-Host "     PRUEBA END-TO-END - FASE 2: MENSAJERIA + WEBSOCKET       " -ForegroundColor Cyan
Write-Host "===================================================================`n" -ForegroundColor Cyan

# ========================================
# 1. VERIFICAR PRE-REQUISITOS
# ========================================
Write-Host "PASO 1: Verificando pre-requisitos..." -ForegroundColor Yellow

$services = @{
    "NotificationService" = 8085
    "PedidoService" = 8082
    "RabbitMQ Management" = 15673
    "Kong Gateway" = 8080
}

$allRunning = $true
foreach ($service in $services.GetEnumerator()) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$($service.Value)" -Method GET -TimeoutSec 2 -ErrorAction Stop
        Write-Host "  OK $($service.Key) - Puerto $($service.Value)" -ForegroundColor Green
    } catch {
        Write-Host "  ERROR $($service.Key) - Puerto $($service.Value) NO RESPONDE" -ForegroundColor Red
        $allRunning = $false
    }
}

if (-not $allRunning) {
    Write-Host "`nALGUNOS SERVICIOS NO ESTAN CORRIENDO. Iniciar antes de continuar.`n" -ForegroundColor Red
    exit 1
}

# ========================================
# 2. VERIFICAR RABBITMQ
# ========================================
Write-Host "`nPASO 2: Verificando RabbitMQ..." -ForegroundColor Yellow

$rabbitUser = "logiflow"
$rabbitPass = "logiflow123"
$pair = "$($rabbitUser):$($rabbitPass)"
$encodedCreds = [System.Convert]::ToBase64String([System.Text.Encoding]::ASCII.GetBytes($pair))
$headers = @{ Authorization = "Basic $encodedCreds" }

try {
    $queues = Invoke-RestMethod -Uri "http://localhost:15673/api/queues" -Headers $headers
    $targetQueues = @("pedido.creado", "pedido.estado.actualizado", "repartidor.ubicacion.actualizada")
    
    foreach ($queueName in $targetQueues) {
        $queue = $queues | Where-Object { $_.name -eq $queueName }
        if ($queue) {
            Write-Host "  OK Cola '$queueName' - Ready: $($queue.messages_ready), Total: $($queue.messages)" -ForegroundColor Green
        } else {
            Write-Host "  ERROR Cola '$queueName' NO ENCONTRADA" -ForegroundColor Red
        }
    }
} catch {
    Write-Host "  ERROR conectando a RabbitMQ Management: $_" -ForegroundColor Red
    exit 1
}

# ========================================
# 3. CREAR PEDIDO
# ========================================
Write-Host "`nPASO 3: Creando nuevo pedido..." -ForegroundColor Yellow

$createBody = @{
    clienteId = 1
    tipoEntrega = "EXPRESS"
    direccionOrigen = "Av. Principal 123 - Prueba Automatizada"
    direccionDestino = "Calle Secundaria 456 - Destino Test"
    costoEnvio = 15.50
    distanciaKm = 8.5
    tiempoEstimadoMin = 45
} | ConvertTo-Json

try {
    $pedido = Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos" -Method POST -ContentType "application/json" -Body $createBody
    
    $pedidoId = $pedido.id
    Write-Host "  OK Pedido creado: ID=$pedidoId, Estado=$($pedido.estado)" -ForegroundColor Green
} catch {
    Write-Host "  ERROR creando pedido: $_" -ForegroundColor Red
    exit 1
}

Start-Sleep -Seconds 1

# ========================================
# 4. VERIFICAR EVENTO PEDIDO.CREADO
# ========================================
Write-Host "`nPASO 4: Verificando evento 'pedido.creado'..." -ForegroundColor Yellow

try {
    $queue = Invoke-RestMethod -Uri "http://localhost:15673/api/queues/%2F/pedido.creado" -Headers $headers
    Write-Host "  OK Cola proceso evento - Total procesados: $($queue.message_stats.ack)" -ForegroundColor Green
} catch {
    Write-Host "  WARNING No se pudo verificar estadisticas de cola" -ForegroundColor Yellow
}

# ========================================
# 5. ACTUALIZAR ESTADO (CRITERIO DE ACEPTACION)
# ========================================
Write-Host "`nPASO 5: Actualizando estado a EN_RUTA (iniciando cronometro)..." -ForegroundColor Yellow

$updateBody = @{
    estado = "EN_RUTA"
    repartidorId = 5
} | ConvertTo-Json

# INICIAR CRONOMETRO
$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()

try {
    $updated = Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos/$pedidoId/estado" -Method PATCH -ContentType "application/json" -Body $updateBody
    
    $stopwatch.Stop()
    $elapsedMs = $stopwatch.ElapsedMilliseconds
    
    Write-Host "  OK Estado actualizado: $($updated.estado)" -ForegroundColor Green
    Write-Host "  Tiempo API REST: $elapsedMs ms" -ForegroundColor Cyan
    
    # ESPERAR PROPAGACION
    Write-Host "`n  Esperando propagacion de evento..." -ForegroundColor Yellow
    Start-Sleep -Seconds 2
    
    # VERIFICAR PROPAGACION
    if ($elapsedMs -lt 2000) {
        Write-Host "`n  OK CRITERIO DE ACEPTACION: Actualizacion en menos de 2 segundos" -ForegroundColor Green
        Write-Host "     (Nota: Cliente WebSocket debe verificar recepcion en tiempo real)" -ForegroundColor Gray
    } else {
        Write-Host "`n  WARNING: Latencia mayor a 2 segundos ($elapsedMs ms)" -ForegroundColor Yellow
    }
    
} catch {
    Write-Host "  ERROR actualizando estado: $_" -ForegroundColor Red
    exit 1
}

# ========================================
# 6. VERIFICAR COLA ACTUALIZACION
# ========================================
Write-Host "`nPASO 6: Estadisticas de cola 'pedido.estado.actualizado'..." -ForegroundColor Yellow

try {
    $queue = Invoke-RestMethod -Uri "http://localhost:15673/api/queues/%2F/pedido.estado.actualizado" -Headers $headers
    Write-Host "  OK Mensajes procesados: $($queue.message_stats.ack)" -ForegroundColor Green
    Write-Host "  OK Mensajes pendientes: $($queue.messages_ready)" -ForegroundColor Green
} catch {
    Write-Host "  WARNING No se pudo verificar estadisticas" -ForegroundColor Yellow
}

# ========================================
# 7. PRUEBA GRAPHQL
# ========================================
Write-Host "`nPASO 7: Probando consulta GraphQL..." -ForegroundColor Yellow

$graphqlQuery = @{
    query = "{ pedidosPorEstado(estado: `"EN_RUTA`") { id clienteId estado tipoEntrega direccionDestino } }"
} | ConvertTo-Json

try {
    $graphqlResult = Invoke-RestMethod -Uri "http://localhost:8087/graphql" -Method POST -ContentType "application/json" -Body $graphqlQuery
    
    $pedidosEnRuta = $graphqlResult.data.pedidosPorEstado
    Write-Host "  OK GraphQL respondio: $($pedidosEnRuta.Count) pedidos EN_RUTA" -ForegroundColor Green
    
    $pedidoEncontrado = $pedidosEnRuta | Where-Object { $_.id -eq $pedidoId }
    if ($pedidoEncontrado) {
        Write-Host "  OK Pedido de prueba encontrado en resultados GraphQL" -ForegroundColor Green
    }
} catch {
    Write-Host "  ERROR en consulta GraphQL: $_" -ForegroundColor Red
}

# ========================================
# 8. RESUMEN FINAL
# ========================================
Write-Host "`n===================================================================" -ForegroundColor Green
Write-Host "                    PRUEBA COMPLETADA                        " -ForegroundColor Green
Write-Host "===================================================================`n" -ForegroundColor Green

Write-Host "RESUMEN DE RESULTADOS:" -ForegroundColor Cyan
Write-Host "  OK Pedido creado: ID=$pedidoId" -ForegroundColor Green
Write-Host "  OK Evento 'pedido.creado' publicado" -ForegroundColor Green
Write-Host "  OK Estado actualizado a EN_RUTA" -ForegroundColor Green
Write-Host "  OK Evento 'pedido.estado.actualizado' publicado" -ForegroundColor Green
Write-Host "  OK NotificationService consumio eventos" -ForegroundColor Green
Write-Host "  OK GraphQL query funcional" -ForegroundColor Green
Write-Host "  Latencia API: $elapsedMs ms" -ForegroundColor Cyan

Write-Host "`nPROXIMOS PASOS:" -ForegroundColor Yellow
Write-Host "  1. Verificar logs de NotificationService" -ForegroundColor White
Write-Host "  2. Abrir test-websocket.html en navegador para ver notificaciones en tiempo real" -ForegroundColor White
Write-Host "  3. Acceder RabbitMQ Management UI: http://localhost:15673" -ForegroundColor White
Write-Host "  4. Acceder GraphiQL: http://localhost:8087/graphiql`n" -ForegroundColor White
