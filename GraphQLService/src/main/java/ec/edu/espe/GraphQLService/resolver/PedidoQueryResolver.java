package ec.edu.espe.GraphQLService.resolver;

import ec.edu.espe.GraphQLService.client.PedidoServiceClient;
import ec.edu.espe.GraphQLService.model.Pedido;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PedidoQueryResolver {

    private final PedidoServiceClient pedidoServiceClient;

    /**
     * Query optimizada con DataLoader
     * Usa batching cuando se solicitan múltiples pedidos
     */
    @QueryMapping
    public CompletableFuture<Pedido> pedido(@Argument Long id, DataFetchingEnvironment env) {
        log.info("GraphQL Query: pedido(id: {}) - Usando DataLoader", id);
        
        DataLoader<Long, Pedido> loader = env.getDataLoader("pedidoLoader");
        if (loader != null) {
            return loader.load(id);
        }
        
        // Fallback si DataLoader no está disponible
        log.warn("DataLoader no disponible, usando llamada directa");
        return CompletableFuture.completedFuture(pedidoServiceClient.getPedidoById(id));
    }

    /**
     * Query sin DataLoader (listado completo)
     * Para filtros específicos no se necesita batching
     */
    @QueryMapping
    public List<Pedido> pedidosPorEstado(@Argument String estado) {
        log.info("GraphQL Query: pedidosPorEstado(estado: {})", estado);
        return pedidoServiceClient.getPedidosByEstado(estado);
    }

    /**
     * Query para listar pedidos con paginación (básica)
     */
    @QueryMapping
    public PedidosResponse pedidos(DataFetchingEnvironment env) {
        log.info("GraphQL Query: pedidos() - Listando todos");
        List<Pedido> pedidos = pedidoServiceClient.getAllPedidos();
        return PedidosResponse.builder()
                .pedidos(pedidos)
                .total(pedidos.size())
                .pagina(1)
                .totalPaginas(1)
                .build();
    }
}

// Clase interna para la respuesta de pedidos
@lombok.Data
@lombok.Builder
class PedidosResponse {
    private List<Pedido> pedidos;
    private int total;
    private int pagina;
    private int totalPaginas;
}
