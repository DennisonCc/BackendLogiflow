# Script para ejecutar pruebas de PedidoService
# Incluye pruebas unitarias, integración y seguridad

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  EJECUTANDO PRUEBAS - PEDIDOSERVICE" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan

Write-Host "`nPruebas a ejecutar:" -ForegroundColor Yellow
Write-Host "  1. Pruebas Unitarias (PedidoServiceUnitTest)" -ForegroundColor White
Write-Host "  2. Pruebas de Integración (PedidoIntegrationTest)" -ForegroundColor White
Write-Host "  3. Pruebas de Seguridad (SecurityTest)" -ForegroundColor White

Write-Host "`n[INFO] Verificando Docker..." -ForegroundColor Blue
docker info > $null 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Docker no está en ejecución. Por favor, inicia Docker Desktop." -ForegroundColor Red
    exit 1
}
Write-Host "[OK] Docker está activo" -ForegroundColor Green

Write-Host "`n[INFO] Cambiando al directorio PedidoService..." -ForegroundColor Blue
Set-Location -Path "$PSScriptRoot\PedidoService"

Write-Host "`n=====================================" -ForegroundColor Cyan
Write-Host "  EJECUTANDO TODAS LAS PRUEBAS" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan

# Ejecutar todas las pruebas
mvn clean test -Dtest="**/*Test"

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n=====================================" -ForegroundColor Green
    Write-Host "  ✓ TODAS LAS PRUEBAS EXITOSAS" -ForegroundColor Green
    Write-Host "=====================================" -ForegroundColor Green
    
    Write-Host "`n[INFO] Generando reporte de cobertura..." -ForegroundColor Blue
    mvn jacoco:report
    
    Write-Host "`n[OK] Reporte de cobertura generado en:" -ForegroundColor Green
    Write-Host "  target/site/jacoco/index.html" -ForegroundColor White
} else {
    Write-Host "`n=====================================" -ForegroundColor Red
    Write-Host "  ✗ ALGUNAS PRUEBAS FALLARON" -ForegroundColor Red
    Write-Host "=====================================" -ForegroundColor Red
    
    Write-Host "`n[INFO] Revisa los logs arriba para más detalles" -ForegroundColor Yellow
    exit 1
}

Write-Host "`n=====================================" -ForegroundColor Cyan
Write-Host "  RESUMEN DE PRUEBAS" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan

Write-Host "`nPruebas implementadas:" -ForegroundColor Yellow
Write-Host "  ✓ Creación de pedido con validación de tipo de entrega (3 tipos)" -ForegroundColor Green
Write-Host "  ✓ Asignación de repartidor disponible" -ForegroundColor Green
Write-Host "  ✓ Rechazo de petición no autenticada (401)" -ForegroundColor Green
Write-Host "  ✓ Validación de permisos y roles" -ForegroundColor Green
Write-Host "  ✓ Pruebas unitarias con Mockito" -ForegroundColor Green
Write-Host "  ✓ Pruebas de integración con TestContainers" -ForegroundColor Green

Write-Host "`nTotal de tests:" -ForegroundColor Yellow
Write-Host "  - PedidoServiceUnitTest: 11 tests" -ForegroundColor White
Write-Host "  - PedidoIntegrationTest: 15 tests" -ForegroundColor White
Write-Host "  - SecurityTest: 20 tests" -ForegroundColor White
Write-Host "  - TOTAL: 46 tests" -ForegroundColor Cyan

Write-Host "`n[INFO] Para ejecutar solo un tipo de prueba:" -ForegroundColor Blue
Write-Host "  mvn test -Dtest=PedidoServiceUnitTest" -ForegroundColor White
Write-Host "  mvn test -Dtest=PedidoIntegrationTest" -ForegroundColor White
Write-Host "  mvn test -Dtest=SecurityTest" -ForegroundColor White
