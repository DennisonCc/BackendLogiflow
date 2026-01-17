package ec.edu.espe.GraphQLService.client;

import ec.edu.espe.GraphQLService.model.Pedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.pedido.url}")
    private String pedidoServiceUrl;

    public Pedido getPedidoById(Long id) {
        try {
            return restTemplate.getForObject(pedidoServiceUrl + "/" + id, Pedido.class);
        } catch (Exception e) {
            log.error("Error fetching pedido {}: {}", id, e.getMessage());
            return null;
        }
    }

    public List<Pedido> getAllPedidos() {
        try {
            ResponseEntity<Pedido[]> response = restTemplate.getForEntity(pedidoServiceUrl, Pedido[].class);
            return response.getBody() != null ? Arrays.asList(response.getBody()) : List.of();
        } catch (Exception e) {
            log.error("Error fetching all pedidos: {}", e.getMessage());
            return List.of();
        }
    }

    public List<Pedido> getPedidosByEstado(String estado) {
        try {
            ResponseEntity<Pedido[]> response = restTemplate.getForEntity(
                    pedidoServiceUrl + "/estado/" + estado, Pedido[].class);
            return response.getBody() != null ? Arrays.asList(response.getBody()) : List.of();
        } catch (Exception e) {
            log.error("Error fetching pedidos by estado {}: {}", estado, e.getMessage());
            return List.of();
        }
    }
}
