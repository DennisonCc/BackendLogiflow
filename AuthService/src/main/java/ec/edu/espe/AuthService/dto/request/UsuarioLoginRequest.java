package ec.edu.espe.AuthService.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

public class UsuarioLoginRequest {
    @NotBlank(message = "no se puede dejar el usuario en blanco")
    private String username;
    @NotBlank(message = "no se puede dejar la contraseña en blanco")
    private String password;
}
