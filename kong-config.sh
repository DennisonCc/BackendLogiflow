#!/bin/bash

echo "================================================"
echo "Configurando Kong API Gateway para Logiflow"
echo "================================================"

# Esperar a que Kong esté listo
echo "Esperando a que Kong esté disponible..."
until curl -s http://localhost:8001/ > /dev/null; do
    sleep 2
    echo "Esperando..."
done
echo "✓ Kong está disponible"

# Crear Service y Route para AuthService
echo ""
echo "Configurando AuthService..."
curl -i -X POST http://localhost:8001/services/ \
  --data "name=auth-service" \
  --data "url=http://host.docker.internal:8081"

curl -i -X POST http://localhost:8001/services/auth-service/routes \
  --data "name=auth-route" \
  --data "paths[]=/api/auth" \
  --data "strip_path=false"

echo "✓ AuthService configurado"

# Crear Service y Route para PedidoService
echo ""
echo "Configurando PedidoService..."
curl -i -X POST http://localhost:8001/services/ \
  --data "name=pedido-service" \
  --data "url=http://host.docker.internal:8082"

curl -i -X POST http://localhost:8001/services/pedido-service/routes \
  --data "name=pedido-route" \
  --data "paths[]=/api/pedidos" \
  --data "strip_path=false"

echo "✓ PedidoService configurado"

# Crear Service y Route para BillingService
echo ""
echo "Configurando BillingService..."
curl -i -X POST http://localhost:8001/services/ \
  --data "name=billing-service" \
  --data "url=http://host.docker.internal:8083"

curl -i -X POST http://localhost:8001/services/billing-service/routes \
  --data "name=billing-route" \
  --data "paths[]=/api/facturas" \
  --data "strip_path=false"

echo "✓ BillingService configurado"

# Crear Service y Route para FleetService
echo ""
echo "Configurando FleetService..."
curl -i -X POST http://localhost:8001/services/ \
  --data "name=fleet-service" \
  --data "url=http://host.docker.internal:8084"

curl -i -X POST http://localhost:8001/services/fleet-service/routes \
  --data "name=fleet-route" \
  --data "paths[]=/api/fleet" \
  --data "strip_path=false"

echo "✓ FleetService configurado"

# Configurar plugin de CORS
echo ""
echo "Configurando CORS..."
curl -i -X POST http://localhost:8001/plugins/ \
  --data "name=cors" \
  --data "config.origins=*" \
  --data "config.methods=GET,POST,PUT,PATCH,DELETE,OPTIONS" \
  --data "config.headers=Accept,Accept-Language,Content-Type,Authorization" \
  --data "config.exposed_headers=X-Auth-Token" \
  --data "config.credentials=true" \
  --data "config.max_age=3600"

echo "✓ CORS configurado"

# Configurar Rate Limiting global
echo ""
echo "Configurando Rate Limiting..."
curl -i -X POST http://localhost:8001/plugins/ \
  --data "name=rate-limiting" \
  --data "config.minute=100" \
  --data "config.hour=1000" \
  --data "config.policy=local"

echo "✓ Rate Limiting configurado"

# Configurar Request/Response Logging
echo ""
echo "Configurando Logging..."
curl -i -X POST http://localhost:8001/plugins/ \
  --data "name=file-log" \
  --data "config.path=/tmp/kong-logs.log"

echo "✓ Logging configurado"

echo ""
echo "================================================"
echo "✓ Configuración de Kong completada exitosamente"
echo "================================================"
echo ""
echo "Puertos:"
echo "  - Kong Proxy: http://localhost:8000"
echo "  - Kong Admin API: http://localhost:8001"
echo "  - Kong Manager (GUI): http://localhost:8002"
echo ""
echo "Servicios configurados:"
echo "  - AuthService: http://localhost:8000/api/auth/*"
echo "  - PedidoService: http://localhost:8000/api/pedidos/*"
echo "  - BillingService: http://localhost:8000/api/facturas/*"
echo "  - FleetService: http://localhost:8000/api/fleet/*"
echo ""
