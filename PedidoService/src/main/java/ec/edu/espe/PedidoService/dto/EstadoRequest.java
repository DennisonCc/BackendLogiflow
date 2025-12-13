package ec.edu.espe.PedidoService.dto;

import ec.edu.espe.PedidoService.model.EstadoPedido;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class EstadoRequest {
        @NotNull
        private EstadoPedido nuevoEstado;

        private Long repartidorId; // Opcional, por si se asigna al cambiar estado

}
