package ec.edu.espe.TrackingService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionRequestDTO {

    @NotNull(message = "repartidorId es requerido")
    private Long repartidorId;

    @NotNull(message = "latitud es requerida")
    private Double latitud;

    @NotNull(message = "longitud es requerida")
    private Double longitud;

    private Long pedidoIdActual;
    private String estado; // EN_RUTA, DISPONIBLE, PAUSADO
    private Double velocidad;
    private Integer precision;
}
