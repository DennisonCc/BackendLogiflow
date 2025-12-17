Write-Host "Configurando rutas en Kong..." -ForegroundColor Yellow

Invoke-RestMethod -Uri "http://localhost:8444/services/" -Method Post -Body @{name="auth-service";url="http://auth-service:8081"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Invoke-RestMethod -Uri "http://localhost:8444/services/auth-service/routes" -Method Post -Body @{name="auth-route";"paths[]"="/api/auth";strip_path="false"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Write-Host "AuthService configurado (publico)"

$consumer = Invoke-RestMethod -Uri "http://localhost:8444/consumers/" -Method Post -Body @{username="logiflow-users"} -ContentType "application/x-www-form-urlencoded"
Write-Host "Consumer JWT creado"

$jwtCred = Invoke-RestMethod -Uri "http://localhost:8444/consumers/logiflow-users/jwt" -Method Post -Body @{key="logiflow-jwt";secret="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";algorithm="HS256"} -ContentType "application/x-www-form-urlencoded"
Write-Host "Credencial JWT configurada"

Invoke-RestMethod -Uri "http://localhost:8444/services/" -Method Post -Body @{name="pedido-service";url="http://pedido-service:8082"} -ContentType "application/x-www-form-urlencoded" | Out-Null
$pedidoRoute = Invoke-RestMethod -Uri "http://localhost:8444/services/pedido-service/routes" -Method Post -Body @{name="pedido-route";"paths[]"="/api/pedidos";strip_path="false"} -ContentType "application/x-www-form-urlencoded"
Invoke-RestMethod -Uri "http://localhost:8444/routes/$($pedidoRoute.id)/plugins" -Method Post -Body @{name="jwt";"config.claims_to_verify"="exp"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Invoke-RestMethod -Uri "http://localhost:8444/routes/$($pedidoRoute.id)/plugins" -Method Post -Body @{name="rate-limiting";"config.minute"=100;"config.policy"="local"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Write-Host "PedidoService configurado (protegido con JWT + Rate Limiting)"

Invoke-RestMethod -Uri "http://localhost:8444/services/" -Method Post -Body @{name="billing-service";url="http://billing-service:8083"} -ContentType "application/x-www-form-urlencoded" | Out-Null
$billingRoute = Invoke-RestMethod -Uri "http://localhost:8444/services/billing-service/routes" -Method Post -Body @{name="billing-route";"paths[]"="/api/facturas";strip_path="false"} -ContentType "application/x-www-form-urlencoded"
Invoke-RestMethod -Uri "http://localhost:8444/routes/$($billingRoute.id)/plugins" -Method Post -Body @{name="jwt";"config.claims_to_verify"="exp"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Invoke-RestMethod -Uri "http://localhost:8444/routes/$($billingRoute.id)/plugins" -Method Post -Body @{name="rate-limiting";"config.minute"=100;"config.policy"="local"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Write-Host "BillingService configurado (protegido con JWT + Rate Limiting)"

Invoke-RestMethod -Uri "http://localhost:8444/services/" -Method Post -Body @{name="fleet-service";url="http://fleet-service:8084"} -ContentType "application/x-www-form-urlencoded" | Out-Null
$fleetRoute = Invoke-RestMethod -Uri "http://localhost:8444/services/fleet-service/routes" -Method Post -Body @{name="fleet-route";"paths[]"="/api/fleet";strip_path="false"} -ContentType "application/x-www-form-urlencoded"
Invoke-RestMethod -Uri "http://localhost:8444/routes/$($fleetRoute.id)/plugins" -Method Post -Body @{name="jwt";"config.claims_to_verify"="exp"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Invoke-RestMethod -Uri "http://localhost:8444/routes/$($fleetRoute.id)/plugins" -Method Post -Body @{name="rate-limiting";"config.minute"=100;"config.policy"="local"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Write-Host "FleetService configurado (protegido con JWT + Rate Limiting)"

Invoke-RestMethod -Uri "http://localhost:8444/plugins/" -Method Post -Body @{name="cors";"config.origins"="*";"config.methods"="GET","HEAD","PUT","PATCH","POST","DELETE","OPTIONS";"config.headers"="Accept,Content-Type,Authorization";"config.credentials"="true"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Write-Host "CORS configurado"

# Rate Limiting Global (para rutas públicas como auth)
Invoke-RestMethod -Uri "http://localhost:8444/plugins/" -Method Post -Body @{name="rate-limiting";"config.minute"=200;"config.hour"=5000;"config.policy"="local"} -ContentType "application/x-www-form-urlencoded" | Out-Null
Write-Host "Rate Limiting Global configurado (200 req/min, 5000 req/hora)"

Write-Host ""
Write-Host "Kong configurado con JWT + Rate Limiting!" -ForegroundColor Green
