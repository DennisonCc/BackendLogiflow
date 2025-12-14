package ec.edu.espe.FleetService.dto.request;

import ec.edu.espe.FleetService.model.EstadoVehiculo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoVehiculoRequest {
    
    @NotNull(message = "El estado es obligatorio")
    private EstadoVehiculo estado;
}
