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
public class PedidoCreadoEvent implements Serializable {
    private String messageId;
    private Long pedidoId;
    private Long clienteId;
    private String tipoEntrega;
    private String direccionOrigen;
    private String direccionDestino;
    private LocalDateTime fechaCreacion;
    private String estado;

    public static PedidoCreadoEvent from(Long pedidoId, Long clienteId, String tipoEntrega,
                                         String direccionOrigen, String direccionDestino,
                                         String estado) {
        return new PedidoCreadoEvent(
                UUID.randomUUID().toString(),
                pedidoId,
                clienteId,
                tipoEntrega,
                direccionOrigen,
                direccionDestino,
                LocalDateTime.now(),
                estado
        );
    }
}
