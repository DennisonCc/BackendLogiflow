Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Insercion de Datos de Prueba - LogiFlow" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Password: "password123" en BCrypt
$bcryptPassword = '`$2a`$10`$N9qo8uLOickgx2ZMRZoMye1IzEXqzJ3vP7wZBuKGdN7Z3qHQVGW0G'

# ============================================
# 1. AUTHDB - Usuarios
# ============================================
Write-Host "[1/4] Insertando usuarios..." -ForegroundColor Yellow

docker exec authdb psql -U postgres -d authdb -c "INSERT INTO usuarios (username, email, password, rol) VALUES ('cliente1', 'cliente1@logiflow.com', '$bcryptPassword', 'Cliente'), ('cliente2', 'cliente2@logiflow.com', '$bcryptPassword', 'Cliente'), ('cliente3', 'maria.lopez@empresa.com', '$bcryptPassword', 'Cliente'), ('cliente4', 'pedro.garcia@startup.ec', '$bcryptPassword', 'Cliente');" | Out-Null

docker exec authdb psql -U postgres -d authdb -c "INSERT INTO usuarios (username, email, password, rol) VALUES ('repartidor1', 'repartidor1@logiflow.com', '$bcryptPassword', 'Repartidor'), ('repartidor2', 'repartidor2@logiflow.com', '$bcryptPassword', 'Repartidor'), ('repartidor3', 'carlos.mendez@logiflow.com', '$bcryptPassword', 'Repartidor');" | Out-Null

docker exec authdb psql -U postgres -d authdb -c "INSERT INTO usuarios (username, email, password, rol) VALUES ('supervisor1', 'supervisor1@logiflow.com', '$bcryptPassword', 'Supervisor'), ('supervisor2', 'ana.torres@logiflow.com', '$bcryptPassword', 'Supervisor');" | Out-Null

docker exec authdb psql -U postgres -d authdb -c "INSERT INTO usuarios (username, email, password, rol) VALUES ('gerente1', 'gerente1@logiflow.com', '$bcryptPassword', 'Gerente'), ('admin', 'admin@logiflow.com', '$bcryptPassword', 'Gerente');" | Out-Null

Write-Host "  OK: 11 usuarios (4 clientes, 3 repartidores, 2 supervisores, 2 gerentes)`n" -ForegroundColor Green

# ============================================
# 2. FLEETDB - Vehiculos y Repartidores
# ============================================
Write-Host "[2/4] Insertando vehiculos y repartidores..." -ForegroundColor Yellow

docker exec fleetdb psql -U postgres -d fleetdb -c "INSERT INTO vehiculos (placa, marca, modelo, estado, tipo_vehiculo) VALUES ('PBX-1234', 'Yamaha', 'FZ16', 'Disponible', 'MOTO'), ('PBX-5678', 'Honda', 'CBR250', 'Disponible', 'MOTO'), ('PCB-9012', 'Chevrolet', 'D-MAX', 'Disponible', 'CAMION'), ('PBA-3456', 'Toyota', 'Hilux', 'Disponible', 'CAMION'), ('PBP-7890', 'Suzuki', 'GN125', 'EnRuta', 'MOTO'), ('PBB-1111', 'Hino', 'Serie 500', 'Disponible', 'CAMION');" | Out-Null

docker exec fleetdb psql -U postgres -d fleetdb -c "INSERT INTO motos (vehiculo_id, cilindraje, tiene_maletero) VALUES (1, '160', true), (2, '250', true), (5, '125', false);" | Out-Null

docker exec fleetdb psql -U postgres -d fleetdb -c "INSERT INTO camiones (vehiculo_id, capacidad_carga, numero_ejes) VALUES (3, 1500.0, 2), (4, 2000.0, 2), (6, 5000.0, 3);" | Out-Null

docker exec fleetdb psql -U postgres -d fleetdb -c "INSERT INTO repartidores (nombre, cedula, telefono, licencia, vehiculo_id) VALUES ('Juan Perez Morales', '1712345678', '0991234567', 'A-123456', 1), ('Andrea Sanchez Ruiz', '1723456789', '0992345678', 'A-234567', 2), ('Carlos Mendoza Torres', '1734567890', '0993456789', 'C-345678', 3), ('Diego Ramirez Castro', '1745678901', '0994567890', 'C-456789', 4), ('Sofia Velasco Gomez', '1756789012', '0995678901', 'A-567890', 5);" | Out-Null

Write-Host "  OK: 6 vehiculos (3 motos, 3 camiones)" -ForegroundColor Green
Write-Host "  OK: 5 repartidores`n" -ForegroundColor Green

# ============================================
# 3. PEDIDODB - Pedidos
# ============================================
Write-Host "[3/4] Insertando pedidos..." -ForegroundColor Yellow

docker exec pedidodb psql -U postgres -d pedidodb -c "INSERT INTO pedidos (cliente_id, direccion_origen, direccion_destino, tipo_entrega, descripcion_paquete, estado, fecha_creacion) VALUES (1, 'Av. Amazonas N24-03, Quito', 'Av. 6 de Diciembre N34-120, Quito', 'Urbana', 'Documentos legales urgentes', 'Recibido', NOW()), (1, 'CC El Jardin, Quito', 'La Carolina, Av. Shyris, Quito', 'Urbana', 'Laptop Dell nueva', 'Asignado', NOW()), (2, 'Centro Historico, Garcia Moreno, Quito', 'Cumbaya, Via Interoceanica, Quito', 'Urbana', 'Paquete de ropa', 'Entregado', NOW()), (3, 'Quitumbe, Terminal Terrestre, Quito', 'Norte, Calderon, Quito', 'Urbana', 'Repuestos automotrices (5kg)', 'Recibido', NOW());" | Out-Null

docker exec pedidodb psql -U postgres -d pedidodb -c "INSERT INTO pedidos (cliente_id, direccion_origen, direccion_destino, tipo_entrega, descripcion_paquete, estado, fecha_creacion) VALUES (2, 'Quito, Av. Mariscal Sucre', 'Sangolqui, Plaza Central', 'Municipal', 'Equipos medicos (15kg)', 'Asignado', NOW()), (4, 'Machachi, Parque Central', 'Quito, Av. Occidental', 'Municipal', 'Flores ornamentales', 'Entregado', NOW()), (1, 'Cayambe, Mercado Municipal', 'Tabacundo, Plaza Principal', 'Municipal', 'Productos lacteos refrigerados', 'Recibido', NOW());" | Out-Null

docker exec pedidodb psql -U postgres -d pedidodb -c "INSERT INTO pedidos (cliente_id, direccion_origen, direccion_destino, tipo_entrega, descripcion_paquete, estado, fecha_creacion) VALUES (3, 'Quito, Av. 10 de Agosto', 'Guayaquil, Malecon 2000', 'Interprovincial', 'Muestras biologicas (urgente)', 'Asignado', NOW()), (4, 'Ambato, Parque Cevallos', 'Quito, La Mariscal', 'Interprovincial', 'Artesanias de cuero (20kg)', 'Entregado', NOW()), (2, 'Cuenca, Centro Historico', 'Loja, Universidad Nacional', 'Interprovincial', 'Libros academicos (30kg)', 'Recibido', NOW());" | Out-Null

docker exec pedidodb psql -U postgres -d pedidodb -c "INSERT INTO pedidos (cliente_id, direccion_origen, direccion_destino, tipo_entrega, descripcion_paquete, estado, fecha_creacion) VALUES (1, 'Quito, Av. Eloy Alfaro', 'Quito, La Floresta', 'Urbana', 'Pedido duplicado', 'Cancelado', NOW()), (3, 'Ibarra, Av. Atahualpa', 'Otavalo, Plaza de Ponchos', 'Municipal', 'Cancelado por cliente', 'Cancelado', NOW());" | Out-Null

Write-Host "  OK: 12 pedidos (4 urbanos, 3 municipales, 3 interprovinciales, 2 cancelados)`n" -ForegroundColor Green

# ============================================
# 4. BILLINGDB - Facturas
# ============================================
Write-Host "[4/4] Insertando facturas..." -ForegroundColor Yellow

docker exec billingdb psql -U postgres -d billingdb -c "INSERT INTO facturas (pedido_id, cliente_id, tarifa_base, total, descripcion, estado, fecha_emision) VALUES (1, 1, 5.50, 5.50, 'Entrega urbana express - Documentos', 'EMITIDA', NOW()), (2, 1, 8.00, 8.00, 'Entrega urbana - Laptop con seguro', 'BORRADOR', NOW()), (3, 2, 6.00, 6.00, 'Entrega urbana - Ropa', 'PAGADA', NOW()), (4, 3, 7.50, 7.50, 'Entrega urbana - Repuestos', 'EMITIDA', NOW());" | Out-Null

docker exec billingdb psql -U postgres -d billingdb -c "INSERT INTO facturas (pedido_id, cliente_id, tarifa_base, total, descripcion, estado, fecha_emision) VALUES (5, 2, 15.00, 15.00, 'Entrega municipal - Equipos medicos', 'EMITIDA', NOW()), (6, 4, 12.00, 12.00, 'Entrega municipal - Flores', 'PAGADA', NOW()), (7, 1, 18.00, 18.00, 'Entrega municipal - Productos refrigerados', 'BORRADOR', NOW());" | Out-Null

docker exec billingdb psql -U postgres -d billingdb -c "INSERT INTO facturas (pedido_id, cliente_id, tarifa_base, total, descripcion, estado, fecha_emision) VALUES (8, 3, 45.00, 45.00, 'Entrega interprovincial urgente', 'EMITIDA', NOW()), (9, 4, 35.00, 35.00, 'Entrega interprovincial - Artesanias', 'PAGADA', NOW()), (10, 2, 50.00, 50.00, 'Entrega interprovincial - Libros', 'EMITIDA', NOW());" | Out-Null

docker exec billingdb psql -U postgres -d billingdb -c "INSERT INTO facturas (pedido_id, cliente_id, tarifa_base, total, descripcion, estado, fecha_emision) VALUES (11, 1, 0.00, 0.00, 'FACTURA ANULADA', 'CANCELADA', NOW()), (12, 3, 0.00, 0.00, 'FACTURA ANULADA', 'CANCELADA', NOW());" | Out-Null

Write-Host "  OK: 12 facturas`n" -ForegroundColor Green

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Datos insertados exitosamente" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "Resumen de datos:" -ForegroundColor Yellow
Write-Host "  authdb:     11 usuarios" -ForegroundColor White
Write-Host "  fleetdb:    6 vehiculos + 5 repartidores" -ForegroundColor White
Write-Host "  pedidodb:   12 pedidos" -ForegroundColor White
Write-Host "  billingdb:  12 facturas" -ForegroundColor White

Write-Host "`nCredenciales (password: 'password123'):" -ForegroundColor Yellow
Write-Host "  Cliente:      cliente1" -ForegroundColor Cyan
Write-Host "  Repartidor:   repartidor1" -ForegroundColor Cyan
Write-Host "  Supervisor:   supervisor1" -ForegroundColor Cyan
Write-Host "  Gerente:      gerente1" -ForegroundColor Cyan
Write-Host "  Admin:        admin`n" -ForegroundColor Cyan
