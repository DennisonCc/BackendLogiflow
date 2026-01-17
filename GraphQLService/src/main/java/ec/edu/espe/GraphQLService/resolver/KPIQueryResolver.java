package ec.edu.espe.GraphQLService.resolver;

import ec.edu.espe.GraphQLService.client.BillingServiceClient;
import ec.edu.espe.GraphQLService.model.KPIDiario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class KPIQueryResolver {

    private final BillingServiceClient billingServiceClient;

    @QueryMapping
    public KPIDiario kpiDiario(@Argument String fecha) {
        log.info("GraphQL Query: kpiDiario(fecha: {})", fecha);
        return billingServiceClient.getKPIDiario(fecha);
    }
}
