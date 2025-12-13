package ec.edu.espe.PedidoService.dto;

import ec.edu.espe.PedidoService.model.TipoEntrega;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PedidoRequest {
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId; // En Fase 2, esto lo sacaremos del Token automáticamente

    @NotBlank(message = "Origen es obligatorio")
    private String direccionOrigen;

    @NotBlank(message = "Destino es obligatorio")
    private String direccionDestino;

    @NotNull(message = "Debe especificar el tipo de entrega")
    private TipoEntrega tipoEntrega;

    @NotBlank
    private String descripcionPaquete;
}