package ec.edu.espe.TrackingService.messaging;

import ec.edu.espe.TrackingService.config.RabbitMQConfig;
import ec.edu.espe.TrackingService.event.RepartidorUbicacionActualizadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUbicacionActualizada(RepartidorUbicacionActualizadaEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY_UBICACION,
                    event
            );
            log.info("Evento publicado: repartidor.ubicacion.actualizada - messageId={}, repartidorId={}",
                    event.getMessageId(), event.getRepartidorId());
        } catch (Exception e) {
            log.error("Error publicando evento repartidor.ubicacion.actualizada: messageId={}, repartidorId={}",
                    event.getMessageId(), event.getRepartidorId(), e);
            throw new RuntimeException("Error publicando evento", e);
        }
    }
}
