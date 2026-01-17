package ec.edu.espe.FleetService.repository;

import ec.edu.espe.FleetService.model.EstadoVehiculo;
import ec.edu.espe.FleetService.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo,Long> {
    Optional<Vehiculo> findByPlaca(String placa);
    List<Vehiculo> findByEstado(EstadoVehiculo estado);
    long countByEstado(EstadoVehiculo estado);
}
