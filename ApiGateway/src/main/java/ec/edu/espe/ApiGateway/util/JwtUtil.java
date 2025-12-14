package ec.edu.espe.ApiGateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public void validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (MalformedJwtException ex) {
            throw new RuntimeException("Token JWT inválido");
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("Token JWT expirado");
        } catch (UnsupportedJwtException ex) {
            throw new RuntimeException("Token JWT no soportado");
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("La cadena claims JWT está vacía");
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
