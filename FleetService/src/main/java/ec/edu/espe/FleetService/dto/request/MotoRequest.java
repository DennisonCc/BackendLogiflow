package ec.edu.espe.FleetService.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(callSuper = true)
public class MotoRequest extends VehiculoRequest {
    @NotNull
    private Integer cilindraje;

    @NotNull
    private Boolean tieneCajon;
}
