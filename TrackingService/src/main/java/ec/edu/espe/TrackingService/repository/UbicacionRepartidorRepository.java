package ec.edu.espe.TrackingService.repository;

import ec.edu.espe.TrackingService.model.UbicacionRepartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UbicacionRepartidorRepository extends JpaRepository<UbicacionRepartidor, Long> {

    @Query("SELECT u FROM UbicacionRepartidor u WHERE u.repartidorId = :repartidorId ORDER BY u.timestamp DESC LIMIT 1")
    Optional<UbicacionRepartidor> findUltimaUbicacionByRepartidorId(Long repartidorId);

    List<UbicacionRepartidor> findByRepartidorIdAndTimestampBetweenOrderByTimestampDesc(
            Long repartidorId, LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT DISTINCT u.repartidorId FROM UbicacionRepartidor u WHERE u.estado = :estado AND u.timestamp > :desde")
    List<Long> findRepartidoresActivosByEstado(String estado, LocalDateTime desde);
}
