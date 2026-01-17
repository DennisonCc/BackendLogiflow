package ec.edu.espe.FleetService.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarUbicacionRequest {

    @NotNull(message = "latitud es requerida")
    private Double latitud;

    @NotNull(message = "longitud es requerida")
    private Double longitud;

    private Long pedidoIdActual;
    private String estado; // EN_RUTA, DISPONIBLE
}
