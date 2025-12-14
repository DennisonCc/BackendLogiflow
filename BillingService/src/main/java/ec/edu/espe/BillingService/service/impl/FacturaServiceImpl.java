package ec.edu.espe.BillingService.service.impl;

import ec.edu.espe.BillingService.dto.request.FacturaRequest;
import ec.edu.espe.BillingService.model.EstadoFactura;
import ec.edu.espe.BillingService.model.Factura;
import ec.edu.espe.BillingService.repository.FacturaRepository;
import ec.edu.espe.BillingService.service.FacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepository;
    private static final BigDecimal TARIFA_BASE_URBANA = new BigDecimal("5.00");

    @Override
    @Transactional
    public Factura generarFactura(FacturaRequest request) {
        // Verificar que no exista ya una factura para este pedido
        facturaRepository.findByPedidoId(request.getPedidoId())
                .ifPresent(f -> {
                    throw new RuntimeException("Ya existe una factura para el pedido: " + request.getPedidoId());
                });

        Factura factura = new Factura();
        factura.setPedidoId(request.getPedidoId());
        factura.setClienteId(request.getClienteId());
        factura.setDescripcion(request.getDescripcion());
        
        // Cálculo de tarifa básica
        BigDecimal tarifaBase = calcularTarifaBase();
        factura.setTarifaBase(tarifaBase);
        factura.setRecargos(BigDecimal.ZERO);
        factura.setDescuentos(BigDecimal.ZERO);
        
        // Total inicial = tarifa base
        BigDecimal total = tarifaBase.add(factura.getRecargos()).subtract(factura.getDescuentos());
        factura.setTotal(total);
        
        factura.setEstado(EstadoFactura.BORRADOR);
        factura.setFechaEmision(LocalDateTime.now());

        return facturaRepository.save(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public Factura obtenerFactura(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Factura obtenerPorPedido(Long pedidoId) {
        return facturaRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RuntimeException("No se encontró factura para el pedido: " + pedidoId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> listarPorCliente(Long clienteId) {
        return facturaRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional
    public Factura emitirFactura(Long id) {
        Factura factura = obtenerFactura(id);
        
        if (factura.getEstado() != EstadoFactura.BORRADOR) {
            throw new RuntimeException("Solo se pueden emitir facturas en estado BORRADOR");
        }
        
        factura.setEstado(EstadoFactura.EMITIDA);
        return facturaRepository.save(factura);
    }

    @Override
    @Transactional
    public Factura marcarComoPagada(Long id) {
        Factura factura = obtenerFactura(id);
        
        if (factura.getEstado() != EstadoFactura.EMITIDA) {
            throw new RuntimeException("Solo se pueden marcar como pagadas facturas EMITIDAS");
        }
        
        factura.setEstado(EstadoFactura.PAGADA);
        factura.setFechaPago(LocalDateTime.now());
        return facturaRepository.save(factura);
    }

    private BigDecimal calcularTarifaBase() {
        // Tarifa base simple - puede ser mejorada con lógica más compleja
        return TARIFA_BASE_URBANA;
    }
}
