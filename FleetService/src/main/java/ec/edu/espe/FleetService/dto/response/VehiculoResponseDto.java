package ec.edu.espe.FleetService.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehiculoResponseDto {
    private Long id;
    private String placa;
    private String marca;
    private String modelo;
    private String estado;
    private String tipoVehiculo;
    
    // Moto specific
    private String cilindraje;
    private Boolean tieneMaletero;
    
    // Camion specific
    private Double capacidadCarga;
    private Integer numeroEjes;
}

