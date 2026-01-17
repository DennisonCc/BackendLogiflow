package ec.edu.espe.FleetService.service;

import ec.edu.espe.FleetService.dto.request.RepartidorRequest;
import ec.edu.espe.FleetService.dto.request.VehiculoRequest;
import ec.edu.espe.FleetService.dto.response.FlotaResumenDto;
import ec.edu.espe.FleetService.model.EstadoVehiculo;
import ec.edu.espe.FleetService.model.Repartidor;
import ec.edu.espe.FleetService.model.Vehiculo;

import java.util.List;

public interface FleetService {
    Vehiculo crearVehiculo(VehiculoRequest request);
    List<Vehiculo> listarVehiculos();
    Vehiculo obtenerVehiculo(Long id);
    Vehiculo actualizarEstadoVehiculo(Long id, EstadoVehiculo estado);
    void eliminarVehiculo(Long id);
    
    Repartidor registrarRepartidor(RepartidorRequest request);
    List<Repartidor> listarRepartidores();
    Repartidor obtenerRepartidor(Long id);
    Repartidor actualizarRepartidor(Long id, RepartidorRequest request);
    void eliminarRepartidor(Long id);
    
    // Nuevos métodos para GraphQL
    FlotaResumenDto obtenerResumenFlota();
    List<Repartidor> obtenerRepartidoresActivos(Long zonaId);
}
