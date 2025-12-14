package ec.edu.espe.ApiGateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service-swagger", r -> r.path("/auth-docs/**")
                        .filters(f -> f.rewritePath("/auth-docs/(?<segment>.*)", "/$\\{segment}"))
                        .uri("http://localhost:8081"))
                .route("pedido-service-swagger", r -> r.path("/pedido-docs/**")
                        .filters(f -> f.rewritePath("/pedido-docs/(?<segment>.*)", "/$\\{segment}"))
                        .uri("http://localhost:8082"))
                .route("fleet-service-swagger", r -> r.path("/fleet-docs/**")
                        .filters(f -> f.rewritePath("/fleet-docs/(?<segment>.*)", "/$\\{segment}"))
                        .uri("http://localhost:8084"))
                .route("billing-service-swagger", r -> r.path("/billing-docs/**")
                        .filters(f -> f.rewritePath("/billing-docs/(?<segment>.*)", "/$\\{segment}"))
                        .uri("http://localhost:8083"))
                .build();
    }
}
