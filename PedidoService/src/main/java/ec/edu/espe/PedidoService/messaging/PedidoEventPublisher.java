package ec.edu.espe.PedidoService.messaging;

import ec.edu.espe.PedidoService.config.RabbitMQConfig;
import ec.edu.espe.PedidoService.event.PedidoCreadoEvent;
import ec.edu.espe.PedidoService.event.PedidoEstadoActualizadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishPedidoCreado(PedidoCreadoEvent event) {
        log.info("Publishing pedido.creado event: messageId={}, pedidoId={}", 
                event.getMessageId(), event.getPedidoId());
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.PEDIDO_CREADO_ROUTING_KEY,
                event
        );
        
        log.info("Event published successfully: pedido.creado for pedidoId={}", event.getPedidoId());
    }

    public void publishPedidoEstadoActualizado(PedidoEstadoActualizadoEvent event) {
        log.info("Publishing pedido.estado.actualizado event: messageId={}, pedidoId={}, estadoNuevo={}", 
                event.getMessageId(), event.getPedidoId(), event.getEstadoNuevo());
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.PEDIDO_ESTADO_ACTUALIZADO_ROUTING_KEY,
                event
        );
        
        log.info("Event published successfully: pedido.estado.actualizado for pedidoId={}", 
                event.getPedidoId());
    }
}
