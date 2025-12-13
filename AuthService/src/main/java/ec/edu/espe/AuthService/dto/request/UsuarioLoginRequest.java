package ec.edu.espe.AuthService.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data

public class UserLoginRequest {
    @NotBlank(message = "no se puede dejar el usuario en blanco")
    private String username;
    @NotBlank(message = "no se puede dejar la contraseña en blanco")
    private String password;
}
