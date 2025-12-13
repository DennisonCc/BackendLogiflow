package ec.edu.espe.FleetService.service;

import ec.edu.espe.FleetService.dto.request.RepartidorRequest;
import ec.edu.espe.FleetService.dto.request.VehiculoRequest;
import ec.edu.espe.FleetService.model.Repartidor;
import ec.edu.espe.FleetService.model.Vehiculo;

import java.util.List;

public interface FleetService {
    Vehiculo crearVehiculo(VehiculoRequest request);
    List<Vehiculo> listarVehiculos();
    Repartidor registrarRepartidor(RepartidorRequest request);
    List<Repartidor> listarRepartidores();
}
