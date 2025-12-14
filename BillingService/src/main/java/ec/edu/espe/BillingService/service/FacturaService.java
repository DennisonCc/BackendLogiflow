package ec.edu.espe.BillingService.service;

import ec.edu.espe.BillingService.dto.request.FacturaRequest;
import ec.edu.espe.BillingService.model.Factura;

import java.util.List;

public interface FacturaService {
    
    Factura generarFactura(FacturaRequest request);
    
    Factura obtenerFactura(Long id);
    
    Factura obtenerPorPedido(Long pedidoId);
    
    List<Factura> listarPorCliente(Long clienteId);
    
    Factura emitirFactura(Long id);
    
    Factura marcarComoPagada(Long id);
}
