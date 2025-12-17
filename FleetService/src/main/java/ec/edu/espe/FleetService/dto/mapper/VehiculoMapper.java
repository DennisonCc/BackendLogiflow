package ec.edu.espe.FleetService.dto.mapper;

import ec.edu.espe.FleetService.dto.request.CamionRequest;
import ec.edu.espe.FleetService.dto.request.MotoRequest;
import ec.edu.espe.FleetService.dto.request.VehiculoRequest;
import ec.edu.espe.FleetService.dto.response.VehiculoResponseDto;
import ec.edu.espe.FleetService.model.Camion;
import ec.edu.espe.FleetService.model.EstadoVehiculo;
import ec.edu.espe.FleetService.model.Moto;
import ec.edu.espe.FleetService.model.Vehiculo;
import org.springframework.stereotype.Component;

@Component
public class VehiculoMapper {
    public Vehiculo toEntity(VehiculoRequest request) {
        if (request == null) {
            return null;
        }

        if (request instanceof MotoRequest motoReq) {
            return Moto.builder()
                    .placa(motoReq.getPlaca())
                    .marca(motoReq.getMarca())
                    .modelo(motoReq.getModelo())
                    .estado(EstadoVehiculo.Disponible)
                    .cilindraje(String.valueOf(motoReq.getCilindraje()))
                    .tieneMaletero(motoReq.getTieneCajon())
                    .build();
        } else if (request instanceof CamionRequest camionReq) {
            return Camion.builder()
                    .placa(camionReq.getPlaca())
                    .marca(camionReq.getMarca())
                    .modelo(camionReq.getModelo())
                    .estado(EstadoVehiculo.Disponible)
                    .capacidadCarga(camionReq.getCapacidadCarga())
                    .numeroEjes(camionReq.getNumeroEjes())
                    .build();
        } else if (request.getTipoVehiculo() != null) {
            // Manejo de VehiculoRequest genérico con tipoVehiculo como String
            String tipo = request.getTipoVehiculo().toUpperCase();
            if ("MOTO".equals(tipo) || "MOTOCICLETA".equals(tipo)) {
                return Moto.builder()
                        .placa(request.getPlaca())
                        .marca(request.getMarca())
                        .modelo(request.getModelo())
                        .estado(EstadoVehiculo.Disponible)
                        .cilindraje("150") // Valor por defecto
                        .tieneMaletero(false)
                        .build();
            } else if ("CAMION".equals(tipo) || "CAMIONETA".equals(tipo) || "AUTO".equals(tipo)) {
                return Camion.builder()
                        .placa(request.getPlaca())
                        .marca(request.getMarca())
                        .modelo(request.getModelo())
                        .estado(EstadoVehiculo.Disponible)
                        .capacidadCarga(request.getCapacidadCarga() != null ? request.getCapacidadCarga() : 0.0)
                        .numeroEjes(2) // Valor por defecto
                        .build();
            }
        }
        throw new IllegalArgumentException("Tipo de vehículo desconocido: " + request.getTipoVehiculo());
    }

    public VehiculoResponseDto toDto(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return null;
        }

        VehiculoResponseDto.VehiculoResponseDtoBuilder builder = VehiculoResponseDto.builder()
                .id(vehiculo.getId())
                .placa(vehiculo.getPlaca())
                .marca(vehiculo.getMarca())
                .modelo(vehiculo.getModelo())
                .estado(vehiculo.getEstado() != null ? vehiculo.getEstado().toString() : "Disponible");

        if (vehiculo instanceof Moto moto) {
            builder.tipoVehiculo("MOTO")
                    .cilindraje(moto.getCilindraje())
                    .tieneMaletero(moto.getTieneMaletero());
        } else if (vehiculo instanceof Camion camion) {
            builder.tipoVehiculo("CAMION")
                    .capacidadCarga(camion.getCapacidadCarga())
                    .numeroEjes(camion.getNumeroEjes());
        }

        return builder.build();
    }
}

