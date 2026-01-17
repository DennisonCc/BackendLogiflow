package ec.edu.espe.NotificationService.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCreadoEvent implements Serializable {
    private String messageId;
    private Long pedidoId;
    private Long clienteId;
    private String tipoEntrega;
    private String direccionOrigen;
    private String direccionDestino;
    private LocalDateTime fechaCreacion;
    private String estado;
}
