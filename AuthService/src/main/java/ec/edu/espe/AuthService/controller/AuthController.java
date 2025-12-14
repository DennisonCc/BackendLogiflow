package ec.edu.espe.AuthService.controller;

import ec.edu.espe.AuthService.dto.request.RefreshTokenRequest;
import ec.edu.espe.AuthService.dto.request.UsuarioLoginRequest;
import ec.edu.espe.AuthService.dto.request.UsuarioRegistroRequest;
import ec.edu.espe.AuthService.dto.response.TokenResponse;
import ec.edu.espe.AuthService.dto.response.UsuarioLogeadoResponse;
import ec.edu.espe.AuthService.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UsuarioLogeadoResponse> login(@Valid @RequestBody UsuarioLoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioRegistroRequest registroRequest) {
        authService.registro(registroRequest);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }
    
    @PostMapping("/token/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }
}
