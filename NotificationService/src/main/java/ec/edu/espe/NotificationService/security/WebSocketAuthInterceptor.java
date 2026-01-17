package ec.edu.espe.NotificationService.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Interceptor para validar JWT en conexiones WebSocket
 * 
 * Valida el token en el handshake STOMP CONNECT
 * El cliente debe enviar el token en el header "Authorization"
 * 
 * Ejemplo de cliente:
 * const socket = new SockJS('/ws');
 * stompClient.connect(
 *   { Authorization: 'Bearer <token>' },
 *   frame => { console.log('Conectado') }
 * );
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JWTProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extraer token del header Authorization
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            
            if (authHeader == null || authHeader.isEmpty()) {
                log.warn("❌ WebSocket CONNECT sin token JWT - Conexión rechazada");
                throw new IllegalArgumentException("Token JWT requerido para conectar a WebSocket");
            }
            
            // Remover prefijo "Bearer " si existe
            String token = authHeader.startsWith("Bearer ") 
                    ? authHeader.substring(7) 
                    : authHeader;
            
            // Validar token
            if (!jwtProvider.validarToken(token)) {
                log.warn("❌ WebSocket CONNECT con token inválido - Conexión rechazada");
                throw new IllegalArgumentException("Token JWT inválido o expirado");
            }
            
            // Extraer username del token
            String username = jwtProvider.getUsernameFromToken(token);
            
            // Crear Authentication y asignarlo al contexto de la sesión
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username, 
                    null, 
                    new ArrayList<>()
            );
            accessor.setUser(authentication);
            
            log.info("✅ WebSocket CONNECT autorizado para usuario: {}", username);
        }
        
        return message;
    }
}
