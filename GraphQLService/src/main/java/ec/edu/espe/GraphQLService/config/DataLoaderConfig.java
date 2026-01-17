package ec.edu.espe.GraphQLService.config;

import ec.edu.espe.GraphQLService.dataloader.PedidoDataLoader;
import graphql.GraphQLContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderOptions;
import org.dataloader.DataLoaderRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataLoaderRegistrar;

/**
 * Configuración de DataLoaders para GraphQL
 * 
 * Los DataLoaders optimizan queries GraphQL mediante batching y caching:
 * - Batching: Agrupa múltiples requests en una sola llamada
 * - Caching: Evita cargar el mismo objeto múltiples veces
 * 
 * Ejemplo de optimización:
 * Query:
 *   pedidos {
 *     id
 *     cliente { nombre }  <- Sin DataLoader: N queries
 *   }
 * 
 * Sin DataLoader: 1 + N queries (1 para pedidos + N para clientes)
 * Con DataLoader: 1 + 1 queries (1 para pedidos + 1 batch para todos los clientes)
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoaderConfig {

    private final PedidoDataLoader pedidoDataLoader;

    @Bean
    public DataLoaderRegistrar dataLoaderRegistrar() {
        return (DataLoaderRegistry registry, GraphQLContext graphQLContext) -> {
            log.info("Registrando DataLoaders...");
            
            // Registrar DataLoader para Pedidos con configuración
            DataLoader<Long, ec.edu.espe.GraphQLService.model.Pedido> pedidoLoader = 
                    DataLoader.newMappedDataLoader(pedidoDataLoader,
                            DataLoaderOptions.newOptions()
                                    .setBatchingEnabled(true)      // Habilitar batching
                                    .setCachingEnabled(true)       // Habilitar cache
                                    .setMaxBatchSize(100));        // Máximo 100 items por batch
            
            registry.register("pedidoLoader", pedidoLoader);
            
            log.info("DataLoader 'pedidoLoader' registrado con batching y caching habilitados");
        };
    }
}
