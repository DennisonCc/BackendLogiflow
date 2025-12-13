package ec.edu.espe.FleetService.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RepartidorRequest {
    @NotBlank
    private String nombre;
    @NotBlank
    private String cedula;
    private String telefono;
    private String licencia;

    private Long vehiculoId;
}
