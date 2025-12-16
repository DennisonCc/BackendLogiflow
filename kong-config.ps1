Write-Host "Configurando rutas en Kong..." -ForegroundColor Yellow

# AuthService
Invoke-RestMethod -Uri "http://localhost:8444/services/" -Method Post -Body @{name="auth-service";url="http://auth-service:8081"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Invoke-RestMethod -Uri "http://localhost:8444/services/auth-service/routes" -Method Post -Body @{name="auth-route";"paths[]"="/api/auth";strip_path="false"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Write-Host " AuthService configurado"

# PedidoService  
Invoke-RestMethod -Uri "http://localhost:8444/services/" -Method Post -Body @{name="pedido-service";url="http://pedido-service:8082"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Invoke-RestMethod -Uri "http://localhost:8444/services/pedido-service/routes" -Method Post -Body @{name="pedido-route";"paths[]"="/api/pedidos";strip_path="false"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Write-Host " PedidoService configurado"

# BillingService
Invoke-RestMethod -Uri "http://localhost:8444/services/" -Method Post -Body @{name="billing-service";url="http://billing-service:8083"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Invoke-RestMethod -Uri "http://localhost:8444/services/billing-service/routes" -Method Post -Body @{name="billing-route";"paths[]"="/api/facturas";strip_path="false"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Write-Host " BillingService configurado"

# FleetService
Invoke-RestMethod -Uri "http://localhost:8444/services/" -Method Post -Body @{name="fleet-service";url="http://fleet-service:8084"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Invoke-RestMethod -Uri "http://localhost:8444/services/fleet-service/routes" -Method Post -Body @{name="fleet-route";"paths[]"="/api/fleet";strip_path="false"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Write-Host " FleetService configurado"

# CORS
Invoke-RestMethod -Uri "http://localhost:8444/plugins/" -Method Post -Body @{name="cors";"config.origins"="*";"config.methods"="GET,POST,PUT,PATCH,DELETE,OPTIONS";"config.headers"="Accept,Content-Type,Authorization";"config.credentials"="true"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Write-Host " CORS configurado"

Write-Host ""
Write-Host "Kong configurado exitosamente!" -ForegroundColor Green
Write-Host "Endpoints disponibles en http://localhost:8080/api/*"
