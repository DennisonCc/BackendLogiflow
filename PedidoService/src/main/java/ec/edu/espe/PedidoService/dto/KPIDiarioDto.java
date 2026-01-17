package ec.edu.espe.PedidoService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KPIDiarioDto {
    private String fecha;
    private Integer pedidosCreados;
    private Integer pedidosCompletados;
    private Integer pedidosCancelados;
    private Double tasaCompletado;
    private Integer tiempoPromedioEntrega;
    private Double distanciaPromedioKm;
    private Double ingresoTotal;
}
