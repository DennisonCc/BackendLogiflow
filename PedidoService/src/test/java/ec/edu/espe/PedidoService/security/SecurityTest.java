package ec.edu.espe.PedidoService.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de Seguridad para PedidoService
 * Valida autenticación (401) y autorización (403)
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Seguridad - Autenticación y Autorización")
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    private final String PEDIDO_REQUEST_BODY = """
            {
                "clienteId": 1,
                "direccionOrigen": "Quito Centro",
                "direccionDestino": "Quito Norte, Cumbayá",
                "tipoEntrega": "Urbana",
                "descripcionPaquete": "Paquete de prueba"
            }
            """;

    // ========== PRUEBAS DE AUTENTICACIÓN (401 Unauthorized) ==========

    @Test
    @DisplayName("Test 1: POST sin autenticación debe retornar 401")
    @WithAnonymousUser
    void postSinAutenticacionDebeRetornar401() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_REQUEST_BODY))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    @DisplayName("Test 2: GET sin autenticación debe retornar 401")
    @WithAnonymousUser
    void getSinAutenticacionDebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    @DisplayName("Test 3: GET lista sin autenticación debe retornar 401")
    @WithAnonymousUser
    void getListaSinAutenticacionDebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/pedidos/cliente/1"))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    @DisplayName("Test 4: PATCH sin autenticación debe retornar 401")
    @WithAnonymousUser
    void patchSinAutenticacionDebeRetornar401() throws Exception {
        String estadoRequest = """
                {
                    "nuevoEstado": "Asignado",
                    "repartidorId": 1
                }
                """;

        mockMvc.perform(patch("/api/pedidos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estadoRequest))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    @DisplayName("Test 5: DELETE sin autenticación debe retornar 401")
    @WithAnonymousUser
    void deleteSinAutenticacionDebeRetornar401() throws Exception {
        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    @DisplayName("Test 6: Múltiples intentos sin autenticación deben retornar 401")
    @WithAnonymousUser
    void multiplesIntentosSinAutenticacion() throws Exception {
        // Intento 1
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_REQUEST_BODY))
                .andExpect(status().isUnauthorized());

        // Intento 2
        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isUnauthorized());

        // Intento 3
        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isUnauthorized());
    }

    // ========== PRUEBAS DE AUTORIZACIÓN CON ROLES VÁLIDOS ==========

    @Test
    @DisplayName("Test 7: Usuario con rol CLIENTE puede crear pedido")
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void clientePuedeCrearPedido() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_REQUEST_BODY))
                .andExpect(status().isOk()); // 200
    }

    @Test
    @DisplayName("Test 8: Usuario con rol SUPERVISOR puede crear pedido")
    @WithMockUser(username = "supervisor1", roles = {"SUPERVISOR"})
    void supervisorPuedeCrearPedido() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_REQUEST_BODY))
                .andExpect(status().isOk()); // 200
    }

    @Test
    @DisplayName("Test 9: Usuario con rol GERENTE puede crear pedido")
    @WithMockUser(username = "gerente1", roles = {"GERENTE"})
    void gerentePuedeCrearPedido() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_REQUEST_BODY))
                .andExpect(status().isOk()); // 200
    }

    @Test
    @DisplayName("Test 10: Usuario con rol REPARTIDOR puede consultar pedidos")
    @WithMockUser(username = "repartidor1", roles = {"REPARTIDOR"})
    void repartidorPuedeConsultarPedidos() throws Exception {
        mockMvc.perform(get("/api/pedidos/cliente/1"))
                .andExpect(status().isOk()); // 200
    }

    // ========== PRUEBAS DE AUTENTICACIÓN VÁLIDA ==========

    @Test
    @DisplayName("Test 11: Usuario autenticado puede realizar POST exitoso")
    @WithMockUser(username = "testuser", roles = {"CLIENTE"})
    void usuarioAutenticadoPuedeAccederPost() throws Exception {
        // Con autenticación válida, la petición se procesa (puede ser 200, 400, etc. pero no 401)
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_REQUEST_BODY))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(401));
    }

    // ========== PRUEBAS DE DIFERENTES MÉTODOS HTTP SIN AUTH ==========

    @Test
    @DisplayName("Test 14: Todos los métodos HTTP sin auth retornan 401")
    @WithAnonymousUser
    void todosMetodosSinAuthRetornan401() throws Exception {
        // POST
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_REQUEST_BODY))
                .andExpect(status().isUnauthorized());

        // GET by ID
        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isUnauthorized());

        // GET by cliente
        mockMvc.perform(get("/api/pedidos/cliente/1"))
                .andExpect(status().isUnauthorized());

        // PATCH
        mockMvc.perform(patch("/api/pedidos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\": \"Asignado\"}"))
                .andExpect(status().isUnauthorized());

        // DELETE
        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isUnauthorized());
    }

    // ========== PRUEBAS DE HEADERS DE AUTENTICACIÓN ==========

    @Test
    @DisplayName("Test 15: Sin header Authorization debe retornar 401")
    void sinHeaderAuthorizationDebeRetornar401() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_REQUEST_BODY))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist("Authorization"));
    }

    @Test
    @DisplayName("Test 16: Petición con usuario válido incluye contexto de seguridad")
    @WithMockUser(username = "testuser", roles = {"CLIENTE"})
    void peticionConUsuarioValidoTieneContextoSeguridad() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_REQUEST_BODY))
                .andExpect(status().isOk());
    }

    // ========== PRUEBAS DE CONSISTENCIA DE SEGURIDAD ==========

    @Test
    @DisplayName("Test 17: Mismo endpoint con y sin auth tiene comportamiento diferente")
    void mismoEndpointComportamientoDiferente() throws Exception {
        // Sin autenticación - debe fallar
        mockMvc.perform(get("/api/pedidos/cliente/1"))
                .andExpect(status().isUnauthorized());

        // Con autenticación - debe pasar (aunque el resultado pueda variar)
        mockMvc.perform(get("/api/pedidos/cliente/1")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("cliente1").roles("CLIENTE")))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(401));
    }

    @Test
    @DisplayName("Test 18: Endpoint protegido rechaza acceso anónimo consistentemente")
    @WithAnonymousUser
    void endpointProtegidoRechazaAccesoAnonimo() throws Exception {
        // Múltiples intentos deben ser rechazados consistentemente
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/pedidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(PEDIDO_REQUEST_BODY))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("Test 19: Usuario autenticado sin rol específico puede acceder")
    @WithMockUser(username = "user", roles = {})
    void usuarioSinRolEspecificoPuedeAcceder() throws Exception {
        // Spring Security con @WithMockUser permite acceso si está autenticado
        // A menos que se especifique @PreAuthorize en el controller
        mockMvc.perform(get("/api/pedidos/cliente/1"))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(401));
    }

    @Test
    @DisplayName("Test 20: Validar que 401 tiene precedencia sobre otros errores")
    @WithAnonymousUser
    void error401TienePrecedenciaSobreOtrosErrores() throws Exception {
        // Incluso con datos inválidos, debe retornar 401 primero
        String requestInvalido = """
                {
                    "clienteId": "invalid",
                    "direccionOrigen": "",
                    "tipoEntrega": "TipoInvalido"
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestInvalido))
                .andExpect(status().isUnauthorized()); // 401, no 400
    }
}
