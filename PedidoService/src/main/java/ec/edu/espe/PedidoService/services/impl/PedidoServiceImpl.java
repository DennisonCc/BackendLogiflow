package ec.edu.espe.PedidoService.services.impl;

import ec.edu.espe.PedidoService.dto.EstadoRequest;
import ec.edu.espe.PedidoService.dto.KPIDiarioDto;
import ec.edu.espe.PedidoService.dto.PedidoRequest;
import ec.edu.espe.PedidoService.event.PedidoCreadoEvent;
import ec.edu.espe.PedidoService.event.PedidoEstadoActualizadoEvent;
import ec.edu.espe.PedidoService.messaging.PedidoEventPublisher;
import ec.edu.espe.PedidoService.model.EstadoPedido;
import ec.edu.espe.PedidoService.model.Pedido;
import ec.edu.espe.PedidoService.repository.PedidoRepository;
import ec.edu.espe.PedidoService.service.CoberturaService;
import ec.edu.espe.PedidoService.services.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
    private final PedidoRepository pedidoRepository;
    private final CoberturaService coberturaService;
    private final PedidoEventPublisher eventPublisher;

    @Override
    @Transactional
    public Pedido crearPedido(PedidoRequest request) {
        // Validar cobertura geográfica
        coberturaService.validarCobertura(request.getDireccionDestino(), request.getTipoEntrega());
        
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setClienteId(request.getClienteId());
        nuevoPedido.setDireccionOrigen(request.getDireccionOrigen());
        nuevoPedido.setDireccionDestino(request.getDireccionDestino());
        nuevoPedido.setTipoEntrega(request.getTipoEntrega());
        nuevoPedido.setDescripcionPaquete(request.getDescripcionPaquete());

        nuevoPedido.setEstado(EstadoPedido.Recibido);
        nuevoPedido.setFechaCreacion(LocalDateTime.now());
        nuevoPedido.setFechaActualizacion(LocalDateTime.now());

        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);
        
        // Publicar evento pedido.creado
        PedidoCreadoEvent event = PedidoCreadoEvent.from(
                pedidoGuardado.getId(),
                pedidoGuardado.getClienteId(),
                pedidoGuardado.getTipoEntrega().name(),
                pedidoGuardado.getDireccionOrigen(),
                pedidoGuardado.getDireccionDestino(),
                pedidoGuardado.getEstado().name()
        );
        eventPublisher.publishPedidoCreado(event);

        return pedidoGuardado;
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
        EstadoPedido estadoAnterior = pedido.getEstado();
        
        pedido.setEstado(request.getNuevoEstado());
        if (request.getRepartidorId() != null) {
            pedido.setRepartidorId(request.getRepartidorId());
        }
        pedido.setFechaActualizacion(LocalDateTime.now());
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        // Publicar evento pedido.estado.actualizado
        PedidoEstadoActualizadoEvent event = PedidoEstadoActualizadoEvent.from(
                pedidoActualizado.getId(),
                estadoAnterior.name(),
                pedidoActualizado.getEstado().name(),
                pedidoActualizado.getRepartidorId()
        );
        eventPublisher.publishPedidoEstadoActualizado(event);
        
        return pedidoActualizado;
    }

    @Override
    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = obtenerPedido(id);
        pedido.setEstado(EstadoPedido.Cancelado);
        pedido.setFechaActualizacion(LocalDateTime.now());
        pedidoRepository.save(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorEstado(String estado) {
        try {
            EstadoPedido estadoPedido = EstadoPedido.valueOf(estado);
            return pedidoRepository.findByEstado(estadoPedido);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + estado);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public KPIDiarioDto obtenerKPIDiario(LocalDate fecha) {
        LocalDateTime inicioDelDia = fecha.atStartOfDay();
        LocalDateTime finDelDia = fecha.plusDays(1).atStartOfDay();

        List<Pedido> pedidosDelDia = pedidoRepository.findByFechaCreacionBetween(inicioDelDia, finDelDia);

        long pedidosCreados = pedidosDelDia.size();
        long pedidosCompletados = pedidosDelDia.stream()
                .filter(p -> p.getEstado() == EstadoPedido.Entregado)
                .count();
        long pedidosCancelados = pedidosDelDia.stream()
                .filter(p -> p.getEstado() == EstadoPedido.Cancelado)
                .count();

        double tasaCompletado = pedidosCreados > 0 
                ? (pedidosCompletados * 100.0) / pedidosCreados 
                : 0.0;

        return KPIDiarioDto.builder()
                .fecha(fecha.toString())
                .pedidosCreados((int) pedidosCreados)
                .pedidosCompletados((int) pedidosCompletados)
                .pedidosCancelados((int) pedidosCancelados)
                .tasaCompletado(tasaCompletado)
                .tiempoPromedioEntrega(0) // Por implementar
                .distanciaPromedioKm(0.0) // Por implementar
                .ingresoTotal(0.0) // Por implementar
                .build();
    }
}
