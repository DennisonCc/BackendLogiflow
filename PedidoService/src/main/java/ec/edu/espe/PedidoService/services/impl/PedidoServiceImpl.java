package ec.edu.espe.PedidoService.services.impl;

import ec.edu.espe.PedidoService.dto.EstadoRequest;
import ec.edu.espe.PedidoService.dto.PedidoRequest;
import ec.edu.espe.PedidoService.model.EstadoPedido;
import ec.edu.espe.PedidoService.model.Pedido;
import ec.edu.espe.PedidoService.repository.PedidoRepository;
import ec.edu.espe.PedidoService.services.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
    private final PedidoRepository pedidoRepository;

    @Override
    @Transactional//ACID IMPLEMETATION :)
    public Pedido crearPedido(PedidoRequest request) {
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setClienteId(request.getClienteId());
        nuevoPedido.setDireccionOrigen(request.getDireccionOrigen());
        nuevoPedido.setDireccionDestino(request.getDireccionDestino());
        nuevoPedido.setTipoEntrega(request.getTipoEntrega());
        nuevoPedido.setDescripcionPaquete(request.getDescripcionPaquete());

        nuevoPedido.setEstado(EstadoPedido.Recibido);
        nuevoPedido.setFechaCreacion(LocalDateTime.now());
        nuevoPedido.setFechaActualizacion(LocalDateTime.now());

        return pedidoRepository.save(nuevoPedido);
    }

    @Override
    @Transactional(readOnly = true)
    public Pedido obtenerPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional
    public Pedido actualizarEstado(Long id, EstadoRequest request) {
        Pedido pedido = obtenerPedido(id);
        pedido.setEstado(request.getNuevoEstado());
        if (request.getRepartidorId() != null) {
            pedido.setRepartidorId(request.getRepartidorId());
        }
        pedido.setFechaActualizacion(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = obtenerPedido(id);
        pedido.setEstado(EstadoPedido.Cancelado);
        pedido.setFechaActualizacion(LocalDateTime.now());
        pedidoRepository.save(pedido);
    }
}
