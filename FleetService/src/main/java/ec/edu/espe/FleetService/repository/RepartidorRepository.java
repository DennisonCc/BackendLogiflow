package ec.edu.espe.FleetService.repository;

import ec.edu.espe.FleetService.model.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepartidorRepository extends JpaRepository<Repartidor,Long> {
    Optional<Repartidor> findByCedula(String cedula);
}
