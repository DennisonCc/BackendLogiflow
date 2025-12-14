package ec.edu.espe.ApiGateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Log de entrada
        logger.info("Incoming request: Method={}, URI={}, RemoteAddress={}", 
                request.getMethod(), 
                request.getURI(), 
                request.getRemoteAddress());

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            
            // Extraer userId si existe
            String userId = request.getHeaders().getFirst("X-User-Id");
            String userInfo = userId != null ? ", UserId=" + userId : "";
            
            // Log de salida
            logger.info("Outgoing response: Method={}, URI={}, StatusCode={}{}", 
                    request.getMethod(),
                    request.getURI(),
                    response.getStatusCode(),
                    userInfo);
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
