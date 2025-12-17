package ec.edu.espe.FleetService.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoRequest {
    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    @NotBlank
    private String marca;

    @NotBlank
    private String modelo;

    @NotNull
    private Double capacidadCarga;

    private String tipoVehiculo; // "CAMION", "MOTO", etc.
}
