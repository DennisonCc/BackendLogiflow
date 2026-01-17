package ec.edu.espe.NotificationService.listener;

import ec.edu.espe.NotificationService.config.RabbitMQConfig;
import ec.edu.espe.NotificationService.event.PedidoCreadoEvent;
import ec.edu.espe.NotificationService.event.PedidoEstadoActualizadoEvent;
import ec.edu.espe.NotificationService.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoEventListener {

    private final NotificationService notificationService;
    private final Set<String> processedMessages = new HashSet<>();

    @RabbitListener(queues = RabbitMQConfig.PEDIDO_CREADO_QUEUE)
    public void handlePedidoCreado(PedidoCreadoEvent event) {
        // Deduplicación por messageId
        if (processedMessages.contains(event.getMessageId())) {
            log.warn("Mensaje duplicado detectado y rechazado: messageId={}", event.getMessageId());
            return;
        }

        log.info("Evento recibido: pedido.creado - messageId={}, pedidoId={}", 
                event.getMessageId(), event.getPedidoId());

        try {
            // Simular envío de notificación
            String mensaje = String.format(
                    "Nuevo pedido #%d creado para cliente %d. Tipo: %s, Destino: %s",
                    event.getPedidoId(), event.getClienteId(), 
                    event.getTipoEntrega(), event.getDireccionDestino()
            );
            
            log.info("📧 [SMS/Email simulado] {}", mensaje);
            
            // Notificar vía WebSocket
            notificationService.notificarPedidoCreado(event);
            
            // Marcar como procesado
            processedMessages.add(event.getMessageId());
            
            log.info("Evento procesado exitosamente: pedido.creado - pedidoId={}", event.getPedidoId());
            
        } catch (Exception e) {
            log.error("Error procesando evento pedido.creado: messageId={}", event.getMessageId(), e);
            throw new RuntimeException("Error procesando evento", e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PEDIDO_ESTADO_ACTUALIZADO_QUEUE)
    public void handlePedidoEstadoActualizado(PedidoEstadoActualizadoEvent event) {
        // Deduplicación por messageId
        if (processedMessages.contains(event.getMessageId())) {
            log.warn("Mensaje duplicado detectado y rechazado: messageId={}", event.getMessageId());
            return;
        }

        log.info("Evento recibido: pedido.estado.actualizado - messageId={}, pedidoId={}, estadoNuevo={}", 
                event.getMessageId(), event.getPedidoId(), event.getEstadoNuevo());

        try {
            // Simular envío de notificación
            String mensaje = String.format(
                    "Pedido #%d cambió de estado: %s → %s",
                    event.getPedidoId(), event.getEstadoAnterior(), event.getEstadoNuevo()
            );
            
            if (event.getRepartidorId() != null) {
                mensaje += String.format(". Repartidor asignado: #%d", event.getRepartidorId());
            }
            
            log.info("📧 [SMS/Email simulado] {}", mensaje);
            
            // Notificar vía WebSocket
            notificationService.notificarEstadoActualizado(event);
            
            // Marcar como procesado
            processedMessages.add(event.getMessageId());
            
            log.info("Evento procesado exitosamente: pedido.estado.actualizado - pedidoId={}", 
                    event.getPedidoId());
            
        } catch (Exception e) {
            log.error("Error procesando evento pedido.estado.actualizado: messageId={}", 
                    event.getMessageId(), e);
            throw new RuntimeException("Error procesando evento", e);
        }
    }
}
