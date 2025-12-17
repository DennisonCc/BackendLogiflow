#!/bin/bash

echo "Configurando rutas en Kong..."

# Esperar a que Kong esté listo
echo "Esperando a que Kong esté disponible..."
until curl -s http://localhost:8444/ > /dev/null; do
    sleep 2
    echo "Esperando..."
done

# Crear Service y Route para AuthService
curl -s -X POST http://localhost:8444/services/ \
  --data "name=auth-service" \
  --data "url=http://auth-service:8081" > /dev/null

curl -s -X POST http://localhost:8444/services/auth-service/routes \
  --data "name=auth-route" \
  --data "paths[]=/api/auth" \
  --data "strip_path=false" > /dev/null

echo "AuthService configurado (publico)"

# Crear Consumer JWT
curl -s -X POST http://localhost:8444/consumers/ \
  --data "username=logiflow-users" > /dev/null

echo "Consumer JWT creado"

# Crear Credencial JWT
curl -s -X POST http://localhost:8444/consumers/logiflow-users/jwt \
  --data "key=logiflow-jwt" \
  --data "secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970" \
  --data "algorithm=HS256" > /dev/null

echo "Credencial JWT configurada"

# Crear Service y Route para PedidoService
curl -s -X POST http://localhost:8444/services/ \
  --data "name=pedido-service" \
  --data "url=http://pedido-service:8082" > /dev/null

PEDIDO_ROUTE_ID=$(curl -s -X POST http://localhost:8444/services/pedido-service/routes \
  --data "name=pedido-route" \
  --data "paths[]=/api/pedidos" \
  --data "strip_path=false" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)

curl -s -X POST http://localhost:8444/routes/$PEDIDO_ROUTE_ID/plugins \
  --data "name=jwt" \
  --data "config.claims_to_verify=exp" > /dev/null

curl -s -X POST http://localhost:8444/routes/$PEDIDO_ROUTE_ID/plugins \
  --data "name=rate-limiting" \
  --data "config.minute=100" \
  --data "config.policy=local" > /dev/null

echo "PedidoService configurado (protegido con JWT + Rate Limiting)"

# Crear Service y Route para BillingService
curl -s -X POST http://localhost:8444/services/ \
  --data "name=billing-service" \
  --data "url=http://billing-service:8083" > /dev/null

BILLING_ROUTE_ID=$(curl -s -X POST http://localhost:8444/services/billing-service/routes \
  --data "name=billing-route" \
  --data "paths[]=/api/facturas" \
  --data "strip_path=false" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)

curl -s -X POST http://localhost:8444/routes/$BILLING_ROUTE_ID/plugins \
  --data "name=jwt" \
  --data "config.claims_to_verify=exp" > /dev/null

curl -s -X POST http://localhost:8444/routes/$BILLING_ROUTE_ID/plugins \
  --data "name=rate-limiting" \
  --data "config.minute=100" \
  --data "config.policy=local" > /dev/null

echo "BillingService configurado (protegido con JWT + Rate Limiting)"

# Crear Service y Route para FleetService
curl -s -X POST http://localhost:8444/services/ \
  --data "name=fleet-service" \
  --data "url=http://fleet-service:8084" > /dev/null

FLEET_ROUTE_ID=$(curl -s -X POST http://localhost:8444/services/fleet-service/routes \
  --data "name=fleet-route" \
  --data "paths[]=/api/fleet" \
  --data "strip_path=false" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)

curl -s -X POST http://localhost:8444/routes/$FLEET_ROUTE_ID/plugins \
  --data "name=jwt" \
  --data "config.claims_to_verify=exp" > /dev/null

curl -s -X POST http://localhost:8444/routes/$FLEET_ROUTE_ID/plugins \
  --data "name=rate-limiting" \
  --data "config.minute=100" \
  --data "config.policy=local" > /dev/null

echo "FleetService configurado (protegido con JWT + Rate Limiting)"

# Configurar plugin de CORS
curl -s -X POST http://localhost:8444/plugins/ \
  --data "name=cors" \
  --data "config.origins=*" \
  --data "config.methods=GET" \
  --data "config.methods=HEAD" \
  --data "config.methods=PUT" \
  --data "config.methods=PATCH" \
  --data "config.methods=POST" \
  --data "config.methods=DELETE" \
  --data "config.methods=OPTIONS" \
  --data "config.headers=Accept" \
  --data "config.headers=Content-Type" \
  --data "config.headers=Authorization" \
  --data "config.credentials=true" > /dev/null

echo "CORS configurado"

# Rate Limiting Global (para rutas públicas como auth)
curl -s -X POST http://localhost:8444/plugins/ \
  --data "name=rate-limiting" \
  --data "config.minute=200" \
  --data "config.hour=5000" \
  --data "config.policy=local" > /dev/null

echo "Rate Limiting Global configurado (200 req/min, 5000 req/hora)"

echo ""
echo "Kong configurado con JWT + Rate Limiting!"
