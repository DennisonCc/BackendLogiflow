package ec.edu.espe.AuthService.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    
    @NotBlank(message = "El token de refresh es obligatorio")
    private String refreshToken;
}
