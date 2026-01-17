package ec.edu.espe.GraphQLService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {
    private Long id;
    private Long clienteId;
    private Long repartidorId;
    private String tipoEntrega;
    private String estado;
    private String direccionOrigen;
    private String direccionDestino;
    private Double costoEnvio;
    private Double distanciaKm;
    private Integer tiempoEstimadoMin;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaEntrega;
    private String observaciones;
}
