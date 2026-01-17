package ec.edu.espe.NotificationService.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PEDIDO_CREADO_QUEUE = "pedido.creado";
    public static final String PEDIDO_ESTADO_ACTUALIZADO_QUEUE = "pedido.estado.actualizado";

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
