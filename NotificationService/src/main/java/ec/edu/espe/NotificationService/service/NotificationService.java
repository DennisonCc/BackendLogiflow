package ec.edu.espe.NotificationService.service;

import ec.edu.espe.NotificationService.event.PedidoCreadoEvent;
import ec.edu.espe.NotificationService.event.PedidoEstadoActualizadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notificarPedidoCreado(PedidoCreadoEvent event) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("tipo", "PEDIDO_CREADO");
        notification.put("pedidoId", event.getPedidoId());
        notification.put("clienteId", event.getClienteId());
        notification.put("estado", event.getEstado());
        notification.put("tipoEntrega", event.getTipoEntrega());
        notification.put("timestamp", LocalDateTime.now());
        notification.put("mensaje", "Nuevo pedido creado");

        // Broadcast a todos los supervisores
        messagingTemplate.convertAndSend("/topic/pedidos", notification);
        
        // Específico para el pedido
        messagingTemplate.convertAndSend("/topic/pedido/" + event.getPedidoId(), notification);
        
        log.info("🔔 WebSocket broadcast: pedido.creado - pedidoId={}", event.getPedidoId());
    }

    public void notificarEstadoActualizado(PedidoEstadoActualizadoEvent event) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("tipo", "PEDIDO_ESTADO_ACTUALIZADO");
        notification.put("pedidoId", event.getPedidoId());
        notification.put("estadoAnterior", event.getEstadoAnterior());
        notification.put("estadoNuevo", event.getEstadoNuevo());
        notification.put("repartidorId", event.getRepartidorId());
        notification.put("timestamp", LocalDateTime.now());
        notification.put("mensaje", "Estado del pedido actualizado a: " + event.getEstadoNuevo());

        // Broadcast a todos los supervisores
        messagingTemplate.convertAndSend("/topic/pedidos", notification);
        
        // Específico para el pedido
        messagingTemplate.convertAndSend("/topic/pedido/" + event.getPedidoId(), notification);
        
        log.info("🔔 WebSocket broadcast: pedido.estado.actualizado - pedidoId={}, estadoNuevo={}", 
                event.getPedidoId(), event.getEstadoNuevo());
    }
}
