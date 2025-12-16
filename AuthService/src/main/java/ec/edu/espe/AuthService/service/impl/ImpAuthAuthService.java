package ec.edu.espe.AuthService.service.impl;

import ec.edu.espe.AuthService.dto.request.UsuarioLoginRequest;
import ec.edu.espe.AuthService.dto.request.UsuarioRegistroRequest;
import ec.edu.espe.AuthService.dto.response.TokenResponse;
import ec.edu.espe.AuthService.dto.response.UsuarioLogeadoResponse;
import ec.edu.espe.AuthService.model.Rol;
import ec.edu.espe.AuthService.model.Usuario;
import ec.edu.espe.AuthService.repository.UsuarioRepository;
import ec.edu.espe.AuthService.seguridad.ProveedorJWT;
import ec.edu.espe.AuthService.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImpAuthAuthService implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final ProveedorJWT proveedorJWT;

    @Override
    public UsuarioLogeadoResponse login(UsuarioLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = proveedorJWT.generarToken(authentication);

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new UsuarioLogeadoResponse(token, usuario.getUsername(), usuario.getRol().name());
    }

    @Override
    public void registro(UsuarioRegistroRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(encoder.encode(request.getPassword()));
        usuario.setRol(Rol.valueOf(request.getRol()));

        usuarioRepository.save(usuario);
    }
    
    @Override
    public TokenResponse refreshToken(String refreshToken) {
        if (!proveedorJWT.validarToken(refreshToken)) {
            throw new RuntimeException("Token de refresh inválido o expirado");
        }
        
        String username = proveedorJWT.obtenerUsernameDelJWT(refreshToken);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        String nuevoAccessToken = proveedorJWT.generarTokenDesdeUsername(username);
        String nuevoRefreshToken = proveedorJWT.generarRefreshToken(username);
        
        return new TokenResponse(nuevoAccessToken, nuevoRefreshToken);
    }
}
