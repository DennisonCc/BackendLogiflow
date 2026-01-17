package ec.edu.espe.GraphQLService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repartidor {
    private Long id;
    private String nombre;
    private String apellido;
    private String cedula;
    private String telefono;
    private String email;
    private String estado;
    private Double calificacionPromedio;
    private Integer pedidosCompletados;
    private Long vehiculoId;
}
