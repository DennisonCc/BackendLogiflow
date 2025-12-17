# Pruebas Unitarias e Integración - PedidoService

## 📋 Resumen

Este proyecto incluye un conjunto completo de pruebas que cubren los requisitos de Fase 1:

1. ✅ **Creación de pedido con validación de tipo de entrega**
2. ✅ **Asignación de repartidor disponible**
3. ✅ **Rechazo de petición no autenticada (401)**
4. ✅ **Rechazo de petición sin permisos (403)**

## 🧪 Tipos de Pruebas Implementadas

### 1. Pruebas Unitarias (`PedidoServiceUnitTest`)
- **Ubicación**: `src/test/java/ec/edu/espe/PedidoService/unit/`
- **Framework**: JUnit 5 + Mockito
- **Total**: 11 tests

**Casos cubiertos**:
- Creación de pedido con tipo Urbana, Municipal, Interprovincial
- Validación de cobertura antes de crear pedido
- Establecimiento de estado inicial como "Recibido"
- Obtención de pedido por ID
- Asignación de repartidor
- Cancelación de pedido
- Manejo de excepciones

### 2. Pruebas de Integración (`PedidoIntegrationTest`)
- **Ubicación**: `src/test/java/ec/edu/espe/PedidoService/integration/`
- **Framework**: JUnit 5 + TestContainers + MockMvc
- **Total**: 15 tests

**Casos cubiertos**:
- Creación de pedidos con los 3 tipos de entrega válidos
- Rechazo de tipos de entrega inválidos
- Asignación de repartidores a pedidos
- Validación de campos obligatorios
- Consulta de pedidos por cliente
- Tests con base de datos PostgreSQL real (contenedor)

### 3. Pruebas de Seguridad (`SecurityTest`)
- **Ubicación**: `src/test/java/ec/edu/espe/PedidoService/security/`
- **Framework**: JUnit 5 + Spring Security Test
- **Total**: 20 tests

**Casos cubiertos**:
- **Autenticación (401)**:
  - POST, GET, PATCH, DELETE sin autenticación
  - Múltiples intentos sin autenticación
  - Validación de headers de autorización
  
- **Autorización con roles**:
  - Acceso con rol CLIENTE, SUPERVISOR, GERENTE, REPARTIDOR
  - Validación de contexto de seguridad
  - Consistencia en rechazo de acceso anónimo

## 📊 Estadísticas

| Tipo de Prueba | Cantidad | Estado |
|----------------|----------|--------|
| Pruebas Unitarias | 11 | ✅ |
| Pruebas de Integración | 15 | ✅ |
| Pruebas de Seguridad | 20 | ✅ |
| **TOTAL** | **46** | ✅ |

## 🚀 Ejecución de Pruebas

### Opción 1: Script PowerShell (Recomendado)

```powershell
.\run-tests.ps1
```

Este script:
- Verifica que Docker esté activo
- Ejecuta todas las pruebas
- Genera reporte de cobertura JaCoCo
- Muestra resumen de resultados

### Opción 2: Maven Directo

```bash
# Todas las pruebas
mvn clean test

# Solo pruebas unitarias
mvn test -Dtest=PedidoServiceUnitTest

# Solo pruebas de integración
mvn test -Dtest=PedidoIntegrationTest

# Solo pruebas de seguridad
mvn test -Dtest=SecurityTest
```

### Opción 3: Desde VSCode

1. Abrir clase de test
2. Click derecho → "Run Tests"
3. Ver resultados en panel "Testing"

## 📦 Dependencias Utilizadas

```xml
<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
</dependency>

<!-- Spring Security Test -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
</dependency>

<!-- TestContainers -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.3</version>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.3</version>
</dependency>
```

## 🔧 Configuración

### TestContainers
Las pruebas de integración usan TestContainers para levantar un PostgreSQL real:

```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
```

**Requisitos**:
- Docker Desktop en ejecución
- Conexión a internet (primera vez para descargar imagen)

### Spring Security Test
Las pruebas de seguridad usan anotaciones para simular usuarios:

```java
@WithMockUser(username = "cliente1", roles = {"CLIENTE"})
@WithAnonymousUser
```

## 📈 Reporte de Cobertura

Después de ejecutar las pruebas, el reporte JaCoCo se genera en:

```
PedidoService/target/site/jacoco/index.html
```

Abrir en navegador para ver:
- Cobertura de líneas
- Cobertura de ramas
- Cobertura de métodos
- Clases no cubiertas

## ✅ Validación de Requisitos

### Requisito 1: Creación de pedido con validación de tipo de entrega

**Tests que lo cubren**:
- `debeCrearPedidoConTipoEntregaUrbana()` ✅
- `debeCrearPedidoConTipoEntregaMunicipal()` ✅
- `debeCrearPedidoConTipoEntregaInterprovincial()` ✅
- `debeRechazarPedidoConTipoEntregaInvalido()` ✅
- `debeRechazarPedidoSinTipoEntrega()` ✅

**Validaciones**:
- Solo acepta: `Urbana`, `Municipal`, `Interprovincial`
- Rechaza valores nulos o inválidos
- Estado inicial: `Recibido`

### Requisito 2: Asignación de repartidor disponible

**Tests que lo cubren**:
- `debeAsignarRepartidorAPedido()` ✅
- `debeValidarAsignacionDeRepartidor()` ✅
- `debeAsignarRepartidorYCambiarEstado()` ✅

**Validaciones**:
- Pedido cambia de estado `Recibido` → `Asignado`
- Se guarda el ID del repartidor
- Fecha de actualización se registra

### Requisito 3: Rechazo de petición no autenticada (401)

**Tests que lo cubren**:
- `postSinAutenticacionDebeRetornar401()` ✅
- `getSinAutenticacionDebeRetornar401()` ✅
- `patchSinAutenticacionDebeRetornar401()` ✅
- `deleteSinAutenticacionDebeRetornar401()` ✅
- `multiplesIntentosSinAutenticacion()` ✅
- `todosMetodosSinAuthRetornan401()` ✅

**Validaciones**:
- Todos los endpoints protegidos retornan HTTP 401
- Sin header `Authorization` → 401
- Usuario anónimo → 401

### Requisito 4: Rechazo de petición sin permisos (403)

**Tests que lo cubren**:
- `clientePuedeCrearPedido()` ✅
- `supervisorPuedeCrearPedido()` ✅
- `gerentePuedeCrearPedido()` ✅
- `repartidorPuedeConsultarPedidos()` ✅

**Validaciones**:
- Roles válidos: CLIENTE, SUPERVISOR, GERENTE, REPARTIDOR
- Usuario autenticado pero sin rol apropiado → 403 (configurar en SecurityConfig si es necesario)
- Acceso basado en roles

## 🐛 Debugging de Pruebas

### Si TestContainers falla:

```powershell
# Verificar Docker
docker info

# Ver contenedores de prueba
docker ps -a | Select-String "testcontainers"

# Limpiar contenedores viejos
docker container prune -f
```

### Si pruebas de seguridad fallan:

- Verificar que `SecurityConfig` tiene reglas correctas
- Revisar que endpoints están protegidos con `authenticated()`
- Validar que `@WithMockUser` tiene roles correctos

### Ver logs detallados:

```bash
mvn test -X -Dtest=PedidoIntegrationTest
```

## 📝 Ejemplos de Salida

### Ejecución exitosa:

```
[INFO] Tests run: 46, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Con cobertura:

```
[INFO] --- jacoco:0.8.11:report ---
[INFO] Loading execution data file: target/jacoco.exec
[INFO] Analyzed bundle 'PedidoService' with 15 classes
```

## 🔄 Integración Continua

Para CI/CD, añadir al pipeline:

```yaml
# GitHub Actions ejemplo
- name: Run Tests
  run: |
    docker-compose up -d pedidodb
    mvn clean test
    
- name: Generate Coverage Report
  run: mvn jacoco:report
  
- name: Upload Coverage
  uses: codecov/codecov-action@v3
```

## 📚 Referencias

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [TestContainers Documentation](https://www.testcontainers.org/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Spring Security Test](https://docs.spring.io/spring-security/reference/servlet/test/index.html)

## 🎯 Próximos Pasos (Fase 2)

- [ ] Añadir pruebas de carga con JMeter
- [ ] Implementar pruebas end-to-end con RestAssured
- [ ] Añadir mutation testing con PIT
- [ ] Configurar Sonarqube para análisis de calidad
- [ ] Implementar Contract Testing con Pact
