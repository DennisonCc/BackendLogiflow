package ec.edu.espe.FleetService.event;

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
                                                            LocalDateTime timestamp, Long pedidoIdActual, String estado) {
        return RepartidorUbicacionActualizadaEvent.builder()
                .messageId(UUID.randomUUID().toString())
                .repartidorId(repartidorId)
                .latitud(latitud)
                .longitud(longitud)
                .timestamp(timestamp != null ? timestamp : LocalDateTime.now())
                .pedidoIdActual(pedidoIdActual)
                .estado(estado)
                .build();
    }
}
