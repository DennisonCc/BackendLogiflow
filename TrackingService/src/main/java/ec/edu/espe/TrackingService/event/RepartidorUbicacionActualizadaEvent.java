package ec.edu.espe.TrackingService.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepartidorUbicacionActualizadaEvent implements Serializable {

    private String messageId;
    private Long repartidorId;
    private Double latitud;
    private Double longitud;
    private LocalDateTime timestamp;
    private Long pedidoIdActual;
    private String estado;
    private Double velocidad;
    private Integer precision;

    public static RepartidorUbicacionActualizadaEvent from(Long repartidorId, Double latitud, Double longitud,
                                                            LocalDateTime timestamp, Long pedidoIdActual, String estado,
                                                            Double velocidad, Integer precision) {
        return RepartidorUbicacionActualizadaEvent.builder()
                .messageId(UUID.randomUUID().toString())
                .repartidorId(repartidorId)
                .latitud(latitud)
                .longitud(longitud)
                .timestamp(timestamp)
                .pedidoIdActual(pedidoIdActual)
                .estado(estado)
                .velocidad(velocidad)
                .precision(precision)
                .build();
    }
}
