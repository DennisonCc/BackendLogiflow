package ec.edu.espe.GraphQLService.dataloader;

import ec.edu.espe.GraphQLService.client.PedidoServiceClient;
import ec.edu.espe.GraphQLService.model.Pedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.MappedBatchLoaderWithContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * DataLoader para Pedidos - Evita N+1 queries
 * 
 * En lugar de hacer N llamadas individuales para obtener N pedidos:
 * pedido(1), pedido(2), pedido(3)...
 * 
 * Hace UNA sola llamada batch:
 * pedidos([1, 2, 3, ...])
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoDataLoader implements MappedBatchLoaderWithContext<Long, Pedido> {

    private final PedidoServiceClient pedidoServiceClient;

    @Override
    public CompletionStage<Map<Long, Pedido>> load(Set<Long> ids, BatchLoaderEnvironment environment) {
        log.info("DataLoader: Cargando batch de {} pedidos", ids.size());
        
        return CompletableFuture.supplyAsync(() -> {
            // En producción, esto haría una llamada optimizada al servicio:
            // GET /api/pedidos/batch?ids=1,2,3,4,5
            
            // Por ahora, simula la carga batch (en realidad hace N queries)
            // TODO: Implementar endpoint batch en PedidoService
            Map<Long, Pedido> pedidoMap = ids.stream()
                    .map(id -> {
                        try {
                            return pedidoServiceClient.getPedidoById(id);
                        } catch (Exception e) {
                            log.warn("Error cargando pedido {}: {}", id, e.getMessage());
                            return null;
                        }
                    })
                    .filter(p -> p != null)
                    .collect(Collectors.toMap(Pedido::getId, p -> p));
            
            log.info("DataLoader: Cargados {} pedidos de {} solicitados", 
                    pedidoMap.size(), ids.size());
            
            return pedidoMap;
        });
    }
}
