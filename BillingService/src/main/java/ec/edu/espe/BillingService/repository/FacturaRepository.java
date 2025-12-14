package ec.edu.espe.BillingService.repository;

import ec.edu.espe.BillingService.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
    Optional<Factura> findByPedidoId(Long pedidoId);
    
    List<Factura> findByClienteId(Long clienteId);
}
