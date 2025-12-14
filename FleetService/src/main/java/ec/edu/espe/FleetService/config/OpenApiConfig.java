package ec.edu.espe.FleetService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FleetService API")
                        .version("1.0")
                        .description("API de gestión de flota (vehículos y repartidores) para LogiFlow")
                        .contact(new Contact()
                                .name("LogiFlow Team")
                                .email("support@logiflow.com")));
    }
}
