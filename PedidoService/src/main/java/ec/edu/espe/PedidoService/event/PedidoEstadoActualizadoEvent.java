package ec.edu.espe.PedidoService.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEstadoActualizadoEvent implements Serializable {
    private String messageId;
    private Long pedidoId;
    private String estadoAnterior;
    private String estadoNuevo;
    private Long repartidorId;
    private LocalDateTime fechaActualizacion;

    public static PedidoEstadoActualizadoEvent from(Long pedidoId, String estadoAnterior,
                                                     String estadoNuevo, Long repartidorId) {
        return new PedidoEstadoActualizadoEvent(
                UUID.randomUUID().toString(),
                pedidoId,
                estadoAnterior,
                estadoNuevo,
                repartidorId,
                LocalDateTime.now()
        );
    }
}
