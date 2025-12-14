package ec.edu.espe.BillingService.dto.response;

import ec.edu.espe.BillingService.model.EstadoFactura;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponse {
    
    private Long id;
    private Long pedidoId;
    private Long clienteId;
    private BigDecimal tarifaBase;
    private BigDecimal recargos;
    private BigDecimal descuentos;
    private BigDecimal total;
    private EstadoFactura estado;
    private String descripcion;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaPago;
}
