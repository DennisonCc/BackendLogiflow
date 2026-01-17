package ec.edu.espe.NotificationService.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

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
}
