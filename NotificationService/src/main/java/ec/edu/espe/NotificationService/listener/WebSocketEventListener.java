package ec.edu.espe.NotificationService.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;

/**
 * Listener para eventos de conexión WebSocket
 * 
 * Registra en logs:
 * - Conexiones y desconexiones
 * - Suscripciones y desuscripciones a tópicos
 * - Información de usuario (si está autenticado)
 */
@Component
@Slf4j
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();
        String sessionId = headerAccessor.getSessionId();
        
        if (user != null) {
            log.info("🔌 WebSocket CONNECT - Usuario: {}, SessionId: {}", 
                    user.getName(), sessionId);
        } else {
            log.info("🔌 WebSocket CONNECT - Usuario: anonymous, SessionId: {}", sessionId);
        }
    }

    @EventListener
    public void handleWebSocketConnectedListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();
        String sessionId = headerAccessor.getSessionId();
        
        if (user != null) {
            log.info("✅ WebSocket CONNECTED - Usuario: {}, SessionId: {}", 
                    user.getName(), sessionId);
        } else {
            log.info("✅ WebSocket CONNECTED - Usuario: anonymous, SessionId: {}", sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();
        String sessionId = headerAccessor.getSessionId();
        
        if (user != null) {
            log.info("🔌 WebSocket DISCONNECT - Usuario: {}, SessionId: {}", 
                    user.getName(), sessionId);
        } else {
            log.info("🔌 WebSocket DISCONNECT - Usuario: anonymous, SessionId: {}", sessionId);
        }
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        
        if (user != null) {
            log.info("📡 WebSocket SUBSCRIBE - Usuario: {}, SessionId: {}, Destination: {}", 
                    user.getName(), sessionId, destination);
        } else {
            log.info("📡 WebSocket SUBSCRIBE - Usuario: anonymous, SessionId: {}, Destination: {}", 
                    sessionId, destination);
        }
    }

    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();
        String sessionId = headerAccessor.getSessionId();
        
        if (user != null) {
            log.info("📡 WebSocket UNSUBSCRIBE - Usuario: {}, SessionId: {}", 
                    user.getName(), sessionId);
        } else {
            log.info("📡 WebSocket UNSUBSCRIBE - Usuario: anonymous, SessionId: {}", sessionId);
        }
    }
}
