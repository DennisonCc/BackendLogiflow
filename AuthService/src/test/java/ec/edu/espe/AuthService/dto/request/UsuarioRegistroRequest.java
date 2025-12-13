package ec.edu.espe.AuthService.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

public class UsuarioRegistroRequest {
    @NotBlank(message = "el nombre de ususario es obligaorio!")
    private String username;
    @NotBlank(message = "la contraseña es obligatoria!")
    private String password;
    @NotBlank(message = "el email es obligatorio!")
    private String email;
    @NotBlank(message = "el rol es obligatorio!")
    private String rol;
}
