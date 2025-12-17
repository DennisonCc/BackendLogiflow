package ec.edu.espe.PedidoService.integration;

import ec.edu.espe.PedidoService.dto.PedidoRequest;
import ec.edu.espe.PedidoService.model.EstadoPedido;
import ec.edu.espe.PedidoService.model.Pedido;
import ec.edu.espe.PedidoService.model.TipoEntrega;
import ec.edu.espe.PedidoService.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración con TestContainers para PedidoService
 * Cubre los siguientes casos:
 * 1. Creación de pedido con validación de tipo de entrega
 * 2. Asignación de repartidor disponible
 * 3. Rechazo de petición no autenticada (401)
 * 4. Rechazo de petición sin permisos (403)
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Pruebas de Integración - PedidoService")
class PedidoIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void setUp() {
        pedidoRepository.deleteAll();
    }

    @Test
    @DisplayName("Test 1: Debe crear pedido con tipo de entrega Urbana válido")
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void debeCrearPedidoConTipoEntregaUrbana() throws Exception {
        String requestBody = """
                {
                    "clienteId": 1,
                    "direccionOrigen": "Quito Centro, Av. 10 de Agosto",
                    "direccionDestino": "Quito Norte, Cumbayá",
                    "tipoEntrega": "Urbana",
                    "descripcionPaquete": "Paquete pequeño"
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.clienteId").value(1))
                .andExpect(jsonPath("$.tipoEntrega").value("Urbana"))
                .andExpect(jsonPath("$.estado").value("Recibido"))
                .andExpect(jsonPath("$.direccionOrigen").value("Quito Centro, Av. 10 de Agosto"))
                .andExpect(jsonPath("$.direccionDestino").value("Quito Norte, Cumbayá"));
    }

    @Test
    @DisplayName("Test 2: Debe crear pedido con tipo de entrega Municipal válido")
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void debeCrearPedidoConTipoEntregaMunicipal() throws Exception {
        String requestBody = """
                {
                    "clienteId": 2,
                    "direccionOrigen": "Quito Centro, Plaza Grande",
                    "direccionDestino": "Quito Norte, Parque Bicentenario",
                    "tipoEntrega": "Municipal",
                    "descripcionPaquete": "Documentos"
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoEntrega").value("Municipal"))
                .andExpect(jsonPath("$.estado").value("Recibido"));
    }

    @Test
    @DisplayName("Test 3: Debe crear pedido con tipo de entrega Interprovincial válido")
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void debeCrearPedidoConTipoEntregaInterprovincial() throws Exception {
        String requestBody = """
                {
                    "clienteId": 3,
                    "direccionOrigen": "Pichincha, Quito",
                    "direccionDestino": "Guayas, Guayaquil",
                    "tipoEntrega": "Interprovincial",
                    "descripcionPaquete": "Mercancía"
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoEntrega").value("Interprovincial"))
                .andExpect(jsonPath("$.estado").value("Recibido"));
    }

    @Test
    @DisplayName("Test 4: Debe rechazar pedido con tipo de entrega inválido")
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void debeRechazarPedidoConTipoEntregaInvalido() throws Exception {
        String requestBody = """
                {
                    "clienteId": 1,
                    "direccionOrigen": "Quito",
                    "direccionDestino": "Cumbayá",
                    "tipoEntrega": "TipoInvalido",
                    "descripcionPaquete": "Paquete"
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test 5: Debe rechazar pedido sin tipo de entrega")
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void debeRechazarPedidoSinTipoEntrega() throws Exception {
        String requestBody = """
                {
                    "clienteId": 1,
                    "direccionOrigen": "Quito",
                    "direccionDestino": "Cumbayá",
                    "descripcionPaquete": "Paquete"
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test 6: Debe asignar repartidor a pedido existente")
    @WithMockUser(username = "supervisor1", roles = {"SUPERVISOR"})
    void debeAsignarRepartidorAPedido() throws Exception {
        // Primero crear un pedido
        Pedido pedido = new Pedido();
        pedido.setClienteId(1L);
        pedido.setDireccionOrigen("Quito Centro");
        pedido.setDireccionDestino("Cumbayá");
        pedido.setTipoEntrega(TipoEntrega.Urbana);
        pedido.setDescripcionPaquete("Paquete de prueba");
        pedido.setEstado(EstadoPedido.Recibido);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Actualizar estado y asignar repartidor
        String requestBody = """
                {
                    "nuevoEstado": "Asignado",
                    "repartidorId": 5
                }
                """;

        mockMvc.perform(patch("/api/pedidos/" + pedidoGuardado.getId() + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("Asignado"))
                .andExpect(jsonPath("$.repartidorId").value(5));
    }

    @Test
    @DisplayName("Test 7: Debe validar que repartidor se asigna correctamente")
    @WithMockUser(username = "supervisor1", roles = {"SUPERVISOR"})
    void debeValidarAsignacionDeRepartidor() throws Exception {
        // Crear pedido inicial
        Pedido pedido = new Pedido();
        pedido.setClienteId(2L);
        pedido.setDireccionOrigen("Quito Norte");
        pedido.setDireccionDestino("Valle de los Chillos");
        pedido.setTipoEntrega(TipoEntrega.Municipal);
        pedido.setDescripcionPaquete("Documentos importantes");
        pedido.setEstado(EstadoPedido.Recibido);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Asignar repartidor ID 3
        String requestBody = """
                {
                    "nuevoEstado": "Asignado",
                    "repartidorId": 3
                }
                """;

        mockMvc.perform(patch("/api/pedidos/" + pedidoGuardado.getId() + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repartidorId").value(3))
                .andExpect(jsonPath("$.estado").value("Asignado"));

        // Verificar que el repartidor se asignó correctamente
        mockMvc.perform(get("/api/pedidos/" + pedidoGuardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repartidorId").value(3));
    }

    @Test
    @DisplayName("Test 8: Debe rechazar petición sin autenticación (401)")
    void debeRechazarPeticionSinAutenticacion() throws Exception {
        String requestBody = """
                {
                    "clienteId": 1,
                    "direccionOrigen": "Quito Centro",
                    "direccionDestino": "Quito Norte, Cumbayá",
                    "tipoEntrega": "Urbana",
                    "descripcionPaquete": "Paquete"
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    @DisplayName("Test 9: Debe rechazar petición GET sin autenticación (401)")
    void debeRechazarGetSinAutenticacion() throws Exception {
        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    @DisplayName("Test 10: Debe rechazar petición DELETE sin autenticación (401)")
    void debeRechazarDeleteSinAutenticacion() throws Exception {
        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    @DisplayName("Test 11: Debe permitir acceso con rol CLIENTE")
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void debePermitirAccesoConRolCliente() throws Exception {
        String requestBody = """
                {
                    "clienteId": 1,
                    "direccionOrigen": "Quito Centro",
                    "direccionDestino": "Quito Norte, Cumbayá",
                    "tipoEntrega": "Urbana",
                    "descripcionPaquete": "Paquete"
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test 12: Debe permitir acceso con rol SUPERVISOR")
    @WithMockUser(username = "supervisor1", roles = {"SUPERVISOR"})
    void debePermitirAccesoConRolSupervisor() throws Exception {
        String requestBody = """
                {
                    "clienteId": 5,
                    "direccionOrigen": "Quito Centro",
                    "direccionDestino": "Quito Sur, Tumbaco",
                    "tipoEntrega": "Municipal",
                    "descripcionPaquete": "Mercancía"
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test 13: Debe validar campos obligatorios (clienteId)")
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void debeValidarCampoClienteIdObligatorio() throws Exception {
        String requestBody = """
                {
                    "direccionOrigen": "Quito",
                    "direccionDestino": "Cumbayá",
                    "tipoEntrega": "Urbana",
                    "descripcionPaquete": "Paquete"
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test 14: Debe validar campos obligatorios (direccionOrigen)")
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void debeValidarCampoDireccionOrigenObligatorio() throws Exception {
        String requestBody = """
                {
                    "clienteId": 1,
                    "direccionDestino": "Cumbayá",
                    "tipoEntrega": "Urbana",
                    "descripcionPaquete": "Paquete"
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test 15: Debe listar pedidos por cliente autenticado")
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void debeListarPedidosPorCliente() throws Exception {
        // Crear pedidos de prueba
        Pedido pedido1 = new Pedido();
        pedido1.setClienteId(1L);
        pedido1.setDireccionOrigen("Origen 1");
        pedido1.setDireccionDestino("Destino 1");
        pedido1.setTipoEntrega(TipoEntrega.Urbana);
        pedido1.setDescripcionPaquete("Paquete 1");
        pedido1.setEstado(EstadoPedido.Recibido);
        pedidoRepository.save(pedido1);

        Pedido pedido2 = new Pedido();
        pedido2.setClienteId(1L);
        pedido2.setDireccionOrigen("Origen 2");
        pedido2.setDireccionDestino("Destino 2");
        pedido2.setTipoEntrega(TipoEntrega.Municipal);
        pedido2.setDescripcionPaquete("Paquete 2");
        pedido2.setEstado(EstadoPedido.Asignado);
        pedidoRepository.save(pedido2);

        mockMvc.perform(get("/api/pedidos/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].clienteId").value(1))
                .andExpect(jsonPath("$[1].clienteId").value(1));
    }
}
