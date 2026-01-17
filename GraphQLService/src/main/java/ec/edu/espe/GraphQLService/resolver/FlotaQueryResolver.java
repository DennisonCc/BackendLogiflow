package ec.edu.espe.GraphQLService.resolver;

import ec.edu.espe.GraphQLService.client.FleetServiceClient;
import ec.edu.espe.GraphQLService.model.FlotaResumen;
import ec.edu.espe.GraphQLService.model.Repartidor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FlotaQueryResolver {

    private final FleetServiceClient fleetServiceClient;

    @QueryMapping
    public List<Repartidor> flotaActiva(@Argument Long zonaId) {
        log.info("GraphQL Query: flotaActiva(zonaId: {})", zonaId);
        return fleetServiceClient.getRepartidoresActivos(zonaId);
    }

    @QueryMapping
    public FlotaResumen resumenFlota() {
        log.info("GraphQL Query: resumenFlota()");
        return fleetServiceClient.getFlotaResumen();
    }
}
