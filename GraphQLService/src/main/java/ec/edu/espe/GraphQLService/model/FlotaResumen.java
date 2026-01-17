package ec.edu.espe.GraphQLService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlotaResumen {
    private Integer totalRepartidores;
    private Integer repartidoresActivos;
    private Integer repartidoresDisponibles;
    private Integer repartidoresEnRuta;
    private Integer vehiculosActivos;
    private Integer pedidosEnCurso;
}
