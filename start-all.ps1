Write-Host "================================================" -ForegroundColor Cyan
Write-Host "   Iniciando BackendLogiflow - Sistema Completo" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "📦 Paso 1: Levantando contenedores con Docker Compose..." -ForegroundColor Yellow
docker-compose up -d --build

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error al levantar los contenedores" -ForegroundColor Red
    exit 1
}

Write-Host " Contenedores iniciados" -ForegroundColor Green
Write-Host ""

Write-Host " Paso 2: Esperando a que Kong esté disponible..." -ForegroundColor Yellow
$maxAttempts = 30
$attempt = 0

do {
    $attempt++
    Start-Sleep -Seconds 2
    Write-Host "  Intento $attempt de $maxAttempts..." -ForegroundColor Gray
    $response = $null
    try { 
        $response = Invoke-WebRequest -Uri "http://localhost:8444/" -UseBasicParsing -TimeoutSec 2 -ErrorAction SilentlyContinue 
    } catch { }
} while ($null -eq $response -and $attempt -lt $maxAttempts)

if ($null -eq $response) {
    Write-Host "❌ Kong no respondió" -ForegroundColor Red
    exit 1
}

Write-Host "✓ Kong está disponible" -ForegroundColor Green
Write-Host ""

Write-Host " Paso 3: Esperando microservicios (60 segundos)..." -ForegroundColor Yellow
Start-Sleep -Seconds 60

Write-Host ""
Write-Host " Paso 4: Configurando rutas en Kong..." -ForegroundColor Yellow
& "$PSScriptRoot\kong-config.ps1"

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "    Sistema iniciado exitosamente" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "🌐 Kong Gateway:  http://localhost:8080" -ForegroundColor Cyan
Write-Host "🌐 Kong Admin:    http://localhost:8444" -ForegroundColor Cyan
Write-Host "🌐 Kong Manager:  http://localhost:8445" -ForegroundColor Cyan
Write-Host ""
