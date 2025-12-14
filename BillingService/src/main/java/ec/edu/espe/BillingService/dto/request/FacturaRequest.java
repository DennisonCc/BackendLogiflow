package ec.edu.espe.BillingService.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FacturaRequest {
    
    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;
    
    private String descripcion;
}
