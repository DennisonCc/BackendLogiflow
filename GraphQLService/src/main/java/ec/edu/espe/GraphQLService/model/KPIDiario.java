package ec.edu.espe.GraphQLService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KPIDiario {
    private String fecha;
    private Integer pedidosCreados;
    private Integer pedidosCompletados;
    private Integer pedidosCancelados;
    private Float tasaCompletado;
    private Integer tiempoPromedioEntrega;
    private Float distanciaPromedioKm;
    private Float ingresoTotal;
}
