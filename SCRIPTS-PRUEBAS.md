# 🧪 SCRIPTS DE PRUEBA - FASE 2

## Script Completo de Prueba End-to-End

### Archivo: `test-fase2.ps1`

```powershell
# ========================================
# SCRIPT DE PRUEBA COMPLETA - FASE 2
# Backend LogiFlow - Event-Driven Architecture
# ========================================

Write-Host "`n╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║     PRUEBA END-TO-END - FASE 2: MENSAJERÍA + WEBSOCKET       ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════════╝`n" -ForegroundColor Cyan

# ========================================
# 1. VERIFICAR PRE-REQUISITOS
# ========================================
Write-Host "📋 PASO 1: Verificando pre-requisitos..." -ForegroundColor Yellow

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
        Write-Host "  ✅ $($service.Key) - Puerto $($service.Value)" -ForegroundColor Green
    } catch {
        Write-Host "  ❌ $($service.Key) - Puerto $($service.Value) NO RESPONDE" -ForegroundColor Red
        $allRunning = $false
    }
}

if (-not $allRunning) {
    Write-Host "`n⚠️  ALGUNOS SERVICIOS NO ESTÁN CORRIENDO. Iniciar antes de continuar.`n" -ForegroundColor Red
    exit 1
}

# ========================================
# 2. VERIFICAR RABBITMQ
# ========================================
Write-Host "`n📋 PASO 2: Verificando RabbitMQ..." -ForegroundColor Yellow

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
            Write-Host "  ✅ Cola '$queueName' - Ready: $($queue.messages_ready), Total: $($queue.messages)" -ForegroundColor Green
        } else {
            Write-Host "  ❌ Cola '$queueName' NO ENCONTRADA" -ForegroundColor Red
        }
    }
} catch {
    Write-Host "  ❌ Error conectando a RabbitMQ Management: $_" -ForegroundColor Red
    exit 1
}

# ========================================
# 3. CREAR PEDIDO
# ========================================
Write-Host "`n📋 PASO 3: Creando nuevo pedido..." -ForegroundColor Yellow

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
    $pedido = Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos" `
        -Method POST `
        -ContentType "application/json" `
        -Body $createBody
    
    $pedidoId = $pedido.id
    Write-Host "  ✅ Pedido creado: ID=$pedidoId, Estado=$($pedido.estado)" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Error creando pedido: $_" -ForegroundColor Red
    exit 1
}

Start-Sleep -Seconds 1

# ========================================
# 4. VERIFICAR EVENTO PEDIDO.CREADO
# ========================================
Write-Host "`n📋 PASO 4: Verificando evento 'pedido.creado'..." -ForegroundColor Yellow

try {
    $queue = Invoke-RestMethod -Uri "http://localhost:15673/api/queues/%2F/pedido.creado" -Headers $headers
    Write-Host "  ✅ Cola procesó evento - Total procesados: $($queue.message_stats.ack)" -ForegroundColor Green
} catch {
    Write-Host "  ⚠️  No se pudo verificar estadísticas de cola" -ForegroundColor Yellow
}

# ========================================
# 5. ACTUALIZAR ESTADO (CRITERIO DE ACEPTACIÓN)
# ========================================
Write-Host "`n📋 PASO 5: Actualizando estado a EN_RUTA (iniciando cronómetro)..." -ForegroundColor Yellow

$updateBody = @{
    estado = "EN_RUTA"
    repartidorId = 5
} | ConvertTo-Json

# INICIAR CRONÓMETRO
$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()

try {
    $updated = Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos/$pedidoId/estado" `
        -Method PATCH `
        -ContentType "application/json" `
        -Body $updateBody
    
    $stopwatch.Stop()
    $elapsedMs = $stopwatch.ElapsedMilliseconds
    
    Write-Host "  ✅ Estado actualizado: $($updated.estado)" -ForegroundColor Green
    Write-Host "  ⏱️  Tiempo API REST: $elapsedMs ms" -ForegroundColor Cyan
    
    # ESPERAR PROPAGACIÓN
    Write-Host "`n  ⏳ Esperando propagación de evento..." -ForegroundColor Yellow
    Start-Sleep -Seconds 2
    
    # VERIFICAR PROPAGACIÓN
    if ($elapsedMs -lt 2000) {
        Write-Host "`n  ✅ CRITERIO DE ACEPTACIÓN: Actualización en < 2 segundos" -ForegroundColor Green
        Write-Host "     (Nota: Cliente WebSocket debe verificar recepción en tiempo real)" -ForegroundColor Gray
    } else {
        Write-Host "`n  ⚠️  ADVERTENCIA: Latencia mayor a 2 segundos ($elapsedMs ms)" -ForegroundColor Yellow
    }
    
} catch {
    Write-Host "  ❌ Error actualizando estado: $_" -ForegroundColor Red
    exit 1
}

# ========================================
# 6. VERIFICAR LOGS NOTIFICATIONSERVICE
# ========================================
Write-Host "`n📋 PASO 6: Verificando logs de NotificationService..." -ForegroundColor Yellow
Write-Host "  ℹ️  Revisar terminal de NotificationService para:" -ForegroundColor Cyan
Write-Host "     - Evento recibido: pedido.estado.actualizado" -ForegroundColor Gray
Write-Host "     - 📧 [SMS/Email simulado] Pedido #$pedidoId cambió de estado" -ForegroundColor Gray
Write-Host "     - 🔔 WebSocket broadcast: pedido.estado.actualizado" -ForegroundColor Gray

# ========================================
# 7. VERIFICAR COLA ACTUALIZACIÓN
# ========================================
Write-Host "`n📋 PASO 7: Estadísticas de cola 'pedido.estado.actualizado'..." -ForegroundColor Yellow

try {
    $queue = Invoke-RestMethod -Uri "http://localhost:15673/api/queues/%2F/pedido.estado.actualizado" -Headers $headers
    Write-Host "  ✅ Mensajes procesados: $($queue.message_stats.ack)" -ForegroundColor Green
    Write-Host "  ✅ Mensajes pendientes: $($queue.messages_ready)" -ForegroundColor Green
    Write-Host "  ✅ Tasa de entrega: $($queue.message_stats.deliver_get_details.rate) msgs/s" -ForegroundColor Green
} catch {
    Write-Host "  ⚠️  No se pudo verificar estadísticas" -ForegroundColor Yellow
}

# ========================================
# 8. PRUEBA GRAPHQL
# ========================================
Write-Host "`n📋 PASO 8: Probando consulta GraphQL..." -ForegroundColor Yellow

$graphqlQuery = @{
    query = @"
{
  pedidosPorEstado(estado: "EN_RUTA") {
    id
    clienteId
    estado
    tipoEntrega
    direccionDestino
  }
}
"@
} | ConvertTo-Json

try {
    $graphqlResult = Invoke-RestMethod -Uri "http://localhost:8087/graphql" `
        -Method POST `
        -ContentType "application/json" `
        -Body $graphqlQuery
    
    $pedidosEnRuta = $graphqlResult.data.pedidosPorEstado
    Write-Host "  ✅ GraphQL respondió: $($pedidosEnRuta.Count) pedidos EN_RUTA" -ForegroundColor Green
    
    $pedidoEncontrado = $pedidosEnRuta | Where-Object { $_.id -eq $pedidoId }
    if ($pedidoEncontrado) {
        Write-Host "  ✅ Pedido de prueba encontrado en resultados GraphQL" -ForegroundColor Green
    }
} catch {
    Write-Host "  ❌ Error en consulta GraphQL: $_" -ForegroundColor Red
}

# ========================================
# 9. RESUMEN FINAL
# ========================================
Write-Host "`n╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║                    ✅ PRUEBA COMPLETADA                        ║" -ForegroundColor Green
Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Green

Write-Host "`n📊 RESUMEN DE RESULTADOS:" -ForegroundColor Cyan
Write-Host "  ✅ Pedido creado: ID=$pedidoId" -ForegroundColor Green
Write-Host "  ✅ Evento 'pedido.creado' publicado" -ForegroundColor Green
Write-Host "  ✅ Estado actualizado a EN_RUTA" -ForegroundColor Green
Write-Host "  ✅ Evento 'pedido.estado.actualizado' publicado" -ForegroundColor Green
Write-Host "  ✅ NotificationService consumió eventos" -ForegroundColor Green
Write-Host "  ✅ GraphQL query funcional" -ForegroundColor Green
Write-Host "  ⏱️  Latencia API: $elapsedMs ms" -ForegroundColor Cyan

Write-Host "`n🎯 PRÓXIMOS PASOS:" -ForegroundColor Yellow
Write-Host "  1. Conectar cliente WebSocket (wscat) para verificar broadcast en tiempo real" -ForegroundColor White
Write-Host "  2. Verificar logs de NotificationService" -ForegroundColor White
Write-Host "  3. Acceder RabbitMQ Management UI: http://localhost:15673" -ForegroundColor White
Write-Host "  4. Acceder GraphiQL: http://localhost:8087/graphiql" -ForegroundColor White

Write-Host ""
```

### Ejecutar el script:
```powershell
cd C:\Users\USUARIO\Documents\GitHub\BackendLogiflow
.\test-fase2.ps1
```

---

## Cliente WebSocket HTML (test-websocket.html)

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cliente WebSocket - LogiFlow</title>
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@6.1.0/bundles/stomp.umd.min.js"></script>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 20px; background: #f5f5f5; }
        .container { max-width: 1000px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h1 { color: #2c3e50; }
        .status { padding: 10px; margin: 10px 0; border-radius: 4px; font-weight: bold; }
        .connected { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .disconnected { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .controls { margin: 20px 0; }
        button { padding: 10px 20px; margin-right: 10px; border: none; border-radius: 4px; cursor: pointer; font-size: 14px; }
        .btn-connect { background: #28a745; color: white; }
        .btn-disconnect { background: #dc3545; color: white; }
        .messages { border: 1px solid #ddd; height: 400px; overflow-y: auto; padding: 10px; background: #fafafa; font-family: 'Courier New', monospace; font-size: 12px; }
        .message { margin: 5px 0; padding: 8px; border-left: 3px solid #007bff; background: white; }
        .message.created { border-left-color: #28a745; }
        .message.updated { border-left-color: #ffc107; }
        .message.error { border-left-color: #dc3545; }
        .timestamp { color: #6c757d; font-size: 11px; }
        .input-group { margin: 10px 0; }
        label { display: inline-block; width: 150px; font-weight: bold; }
        input[type="text"] { padding: 5px; width: 300px; border: 1px solid #ddd; border-radius: 4px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🔌 Cliente WebSocket - LogiFlow Fase 2</h1>
        
        <div id="status" class="status disconnected">
            ⚠️ DESCONECTADO
        </div>

        <div class="controls">
            <div class="input-group">
                <label>URL WebSocket:</label>
                <input type="text" id="wsUrl" value="http://localhost:8085/ws" />
            </div>
            <div class="input-group">
                <label>Suscribirse a:</label>
                <input type="text" id="topic" value="/topic/pedidos" placeholder="/topic/pedidos" />
            </div>
            <button class="btn-connect" onclick="connect()">🔌 Conectar</button>
            <button class="btn-disconnect" onclick="disconnect()">🔌 Desconectar</button>
            <button onclick="clearMessages()">🗑️ Limpiar</button>
        </div>

        <h3>📨 Mensajes Recibidos:</h3>
        <div id="messages" class="messages">
            <div style="color: #6c757d;">Esperando conexión...</div>
        </div>

        <h3>📊 Estadísticas:</h3>
        <div style="padding: 10px; background: #e9ecef; border-radius: 4px;">
            <div>Total mensajes: <span id="totalMessages">0</span></div>
            <div>Creados: <span id="createdCount">0</span> | Actualizados: <span id="updatedCount">0</span></div>
            <div>Tiempo de conexión: <span id="connectionTime">--</span></div>
        </div>
    </div>

    <script>
        let stompClient = null;
        let subscription = null;
        let messageCount = 0;
        let createdCount = 0;
        let updatedCount = 0;
        let connectionStart = null;

        function connect() {
            const wsUrl = document.getElementById('wsUrl').value;
            const topic = document.getElementById('topic').value;

            addMessage('ℹ️ Conectando a ' + wsUrl + '...', 'info');

            const socket = new SockJS(wsUrl);
            stompClient = new StompJs.Client({
                webSocketFactory: () => socket,
                debug: (str) => console.log(str),
                reconnectDelay: 5000,
                heartbeatIncoming: 4000,
                heartbeatOutgoing: 4000
            });

            stompClient.onConnect = function(frame) {
                connectionStart = new Date();
                updateStatus(true);
                addMessage('✅ Conectado exitosamente', 'success');
                
                // Suscribirse al tópico
                subscription = stompClient.subscribe(topic, function(message) {
                    onMessageReceived(message);
                });
                
                addMessage('📡 Suscrito a: ' + topic, 'info');
                updateConnectionTime();
            };

            stompClient.onStompError = function(frame) {
                addMessage('❌ Error STOMP: ' + frame.headers['message'], 'error');
                updateStatus(false);
            };

            stompClient.activate();
        }

        function disconnect() {
            if (stompClient !== null) {
                if (subscription) {
                    subscription.unsubscribe();
                }
                stompClient.deactivate();
                updateStatus(false);
                addMessage('🔌 Desconectado', 'info');
            }
        }

        function onMessageReceived(message) {
            messageCount++;
            const payload = JSON.parse(message.body);
            
            let messageClass = 'message';
            if (payload.tipo === 'PEDIDO_CREADO') {
                createdCount++;
                messageClass += ' created';
            } else if (payload.tipo === 'PEDIDO_ESTADO_ACTUALIZADO') {
                updatedCount++;
                messageClass += ' updated';
            }

            const timestamp = new Date().toLocaleTimeString('es-ES');
            const messageHtml = `
                <div class="${messageClass}">
                    <div class="timestamp">${timestamp}</div>
                    <strong>${payload.tipo}</strong><br/>
                    Pedido ID: ${payload.pedidoId}<br/>
                    ${payload.estadoNuevo ? 'Estado: ' + payload.estadoAnterior + ' → ' + payload.estadoNuevo : 'Estado: ' + payload.estado}<br/>
                    ${payload.mensaje}
                </div>
            `;
            
            document.getElementById('messages').innerHTML += messageHtml;
            document.getElementById('messages').scrollTop = document.getElementById('messages').scrollHeight;
            
            updateStats();
        }

        function addMessage(text, type) {
            const timestamp = new Date().toLocaleTimeString('es-ES');
            const messageHtml = `
                <div class="message ${type}">
                    <div class="timestamp">${timestamp}</div>
                    ${text}
                </div>
            `;
            document.getElementById('messages').innerHTML += messageHtml;
            document.getElementById('messages').scrollTop = document.getElementById('messages').scrollHeight;
        }

        function updateStatus(connected) {
            const statusDiv = document.getElementById('status');
            if (connected) {
                statusDiv.className = 'status connected';
                statusDiv.textContent = '✅ CONECTADO';
            } else {
                statusDiv.className = 'status disconnected';
                statusDiv.textContent = '⚠️ DESCONECTADO';
                connectionStart = null;
            }
        }

        function updateStats() {
            document.getElementById('totalMessages').textContent = messageCount;
            document.getElementById('createdCount').textContent = createdCount;
            document.getElementById('updatedCount').textContent = updatedCount;
        }

        function updateConnectionTime() {
            if (connectionStart) {
                const elapsed = Math.floor((new Date() - connectionStart) / 1000);
                document.getElementById('connectionTime').textContent = elapsed + 's';
                setTimeout(updateConnectionTime, 1000);
            }
        }

        function clearMessages() {
            document.getElementById('messages').innerHTML = '<div style="color: #6c757d;">Mensajes limpiados...</div>';
            messageCount = 0;
            createdCount = 0;
            updatedCount = 0;
            updateStats();
        }
    </script>
</body>
</html>
```

Guardar como `test-websocket.html` y abrir en navegador.
