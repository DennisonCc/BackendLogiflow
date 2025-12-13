package ec.edu.espe.FleetService.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CamionRequest extends VehiculoRequest {
    @NotNull
    private Double capacidadCarga;

    @NotNull
    private Integer numeroEjes;
}
