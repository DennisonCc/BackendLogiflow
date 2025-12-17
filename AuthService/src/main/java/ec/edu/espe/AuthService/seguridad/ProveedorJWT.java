package ec.edu.espe.AuthService.seguridad;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class ProveedorJWT {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationDate;
    
    @Value("${jwt.refresh-expiration:604800000}") // 7 días por defecto
    private long jwtRefreshExpirationDate;

    public String generarToken(Authentication authentication) {
        String username = authentication.getName();
        Date fechaActual = new Date();
        Date fechaExpiracion = new Date(fechaActual.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(username)
                .setIssuer("logiflow-jwt") // Kong espera este claim
                .setIssuedAt(new Date())
                .setExpiration(fechaExpiracion)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String generarTokenDesdeUsername(String username) {
        Date fechaActual = new Date();
        Date fechaExpiracion = new Date(fechaActual.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(username)
                .setIssuer("logiflow-jwt") // Kong espera este claim
                .setIssuedAt(new Date())
                .setExpiration(fechaExpiracion)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String generarRefreshToken(String username) {
        Date fechaActual = new Date();
        Date fechaExpiracion = new Date(fechaActual.getTime() + jwtRefreshExpirationDate);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(fechaExpiracion)
                .claim("type", "refresh")
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        // Kong espera el secret como string hexadecimal, no como Base64 decodificado
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String obtenerUsernameDelJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            System.out.println("Token JWT inválido");
        } catch (ExpiredJwtException ex) {
            System.out.println("Token JWT expirado");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Token JWT no soportado");
        } catch (IllegalArgumentException ex) {
            System.out.println("La cadena claims JWT está vacía");
        }
        return false;
    }
}
