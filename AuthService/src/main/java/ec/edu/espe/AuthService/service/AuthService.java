package ec.edu.espe.AuthService.service;

import ec.edu.espe.AuthService.dto.request.UsuarioLoginRequest;
import ec.edu.espe.AuthService.dto.request.UsuarioRegistroRequest;
import ec.edu.espe.AuthService.dto.response.TokenResponse;
import ec.edu.espe.AuthService.dto.response.UsuarioLogeadoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service

public interface AuthService {
    UsuarioLogeadoResponse login(UsuarioLoginRequest request);
    void registro(UsuarioRegistroRequest request);
    TokenResponse refreshToken(String refreshToken);
}
