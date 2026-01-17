package ec.edu.espe.TrackingService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UbicacionResponseDTO {

    private Long id;
    private Long repartidorId;
    private Double latitud;
    private Double longitud;
    private LocalDateTime timestamp;
    private Long pedidoIdActual;
    private String estado;
    private Double velocidad;
    private Integer precision;
}
