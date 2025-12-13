package ec.edu.espe.FleetService.dto.request;


import lombok.Data;
import jakarta.validation.constraints.NotBlank;


@Data
public abstract class VehiculoRequest {
    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    @NotBlank
    private String marca;

    @NotBlank
    private String modelo;




}
