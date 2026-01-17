package ec.edu.espe.PedidoService.repository;

import ec.edu.espe.PedidoService.model.EstadoPedido;
import ec.edu.espe.PedidoService.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByRepartidorId(Long repartidorId);
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
}
