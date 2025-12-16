# Script para iniciar todo el sistema BackendLogiflow

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "   Iniciando BackendLogiflow - Sistema Completo" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Paso 1: Levantar todos los contenedores
Write-Host "ðŸ“¦ Paso 1: Levantando contenedores con Docker Compose..." -ForegroundColor Yellow
docker-compose up -d --build

if ($LASTEXITCODE -ne 0) {
    Write-Host "âŒ Error al levantar los contenedores" -ForegroundColor Red
    exit 1
}

Write-Host "âœ“ Contenedores iniciados" -ForegroundColor Green
Write-Host ""

# Paso 2: Esperar a que Kong estÃ© listo
Write-Host "â³ Paso 2: Esperando a que Kong estÃ© disponible..." -ForegroundColor Yellow
$maxAttempts = 30
$attempt = 0

do {
    $attempt++
    Start-Sleep -Seconds 2
    Write-Host "  Intento $attempt de $maxAttempts..." -ForegroundColor Gray
    $response = try { 
        Invoke-WebRequest -Uri "http://localhost:8001/" -UseBasicParsing -TimeoutSec 2 -ErrorAction SilentlyContinue 
    } catch { 
        $null 
    }
} while ($null -eq $response -and $attempt -lt $maxAttempts)

if ($null -eq $response) {
    Write-Host "âŒ Kong no respondiÃ³ despuÃ©s de $maxAttempts intentos" -ForegroundColor Red
    Write-Host "   Revisa los logs con: docker logs kong-gateway" -ForegroundColor Yellow
    exit 1
}

Write-Host "âœ“ Kong estÃ¡ disponible" -ForegroundColor Green
Write-Host ""

# Paso 3: Esperar a que los microservicios estÃ©n listos
Write-Host "â³ Paso 3: Esperando a que los microservicios estÃ©n listos..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

$services = @(
    @{Name="AuthService"; Port=8081},
    @{Name="PedidoService"; Port=8082},
    @{Name="BillingService"; Port=8083},
    @{Name="FleetService"; Port=8084}
)

foreach ($service in $services) {
    $maxServiceAttempts = 20
    $serviceAttempt = 0
    $ready = $false
    
    while (-not $ready -and $serviceAttempt -lt $maxServiceAttempts) {
        $serviceAttempt++
        $healthResponse = $null
        try {
            $healthResponse = Invoke-WebRequest -Uri "http://localhost:$($service.Port)/actuator/health" -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop
            if ($healthResponse.StatusCode -eq 200) {
                $ready = $true
                Write-Host "  âœ“ $($service.Name) estÃ¡ listo" -ForegroundColor Green
            }
        }
        catch {
            Write-Host "  Esperando $($service.Name)... (intento $serviceAttempt)" -ForegroundColor Gray
            Start-Sleep -Seconds 3
        }
    }
    
    if (-not $ready) {
        Write-Host "  âš  $($service.Name) no respondiÃ³, continuando de todas formas..." -ForegroundColor Yellow
    }
}

Write-Host ""

# Paso 4: Configurar Kong
Write-Host "ðŸ”§ Paso 4: Configurando rutas y plugins en Kong..." -ForegroundColor Yellow
& "$PSScriptRoot\kong-config.ps1"

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "   âœ… Sistema BackendLogiflow iniciado exitosamente" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "ðŸŒ Acceso al sistema:" -ForegroundColor White
Write-Host "  Kong Gateway (API):  http://localhost:8000" -ForegroundColor Cyan
Write-Host "  Kong Admin API:      http://localhost:8001" -ForegroundColor Cyan
Write-Host "  Kong Manager (GUI):  http://localhost:8002" -ForegroundColor Cyan
Write-Host ""
Write-Host "ðŸ“Š Microservicios:" -ForegroundColor White
Write-Host "  AuthService:         http://localhost:8081" -ForegroundColor Gray
Write-Host "  PedidoService:       http://localhost:8082" -ForegroundColor Gray
Write-Host "  BillingService:      http://localhost:8083" -ForegroundColor Gray
Write-Host "  FleetService:        http://localhost:8084" -ForegroundColor Gray
Write-Host ""
Write-Host "ðŸ”— Endpoints (a travÃ©s de Kong):" -ForegroundColor White
Write-Host "  http://localhost:8000/api/auth/*" -ForegroundColor Cyan
Write-Host "  http://localhost:8000/api/pedidos/*" -ForegroundColor Cyan
Write-Host "  http://localhost:8000/api/facturas/*" -ForegroundColor Cyan
Write-Host "  http://localhost:8000/api/fleet/*" -ForegroundColor Cyan
Write-Host ""
Write-Host "ðŸ“ Comandos Ãºtiles:" -ForegroundColor White
Write-Host "  Ver logs:            docker-compose logs -f" -ForegroundColor Gray
Write-Host "  Ver estado:          docker-compose ps" -ForegroundColor Gray
Write-Host "  Detener todo:        docker-compose down" -ForegroundColor Gray
Write-Host "  Reiniciar:           docker-compose restart" -ForegroundColor Gray
Write-Host ""

