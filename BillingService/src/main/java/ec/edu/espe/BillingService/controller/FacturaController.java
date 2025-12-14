package ec.edu.espe.BillingService.controller;

import ec.edu.espe.BillingService.dto.request.FacturaRequest;
import ec.edu.espe.BillingService.model.Factura;
import ec.edu.espe.BillingService.service.FacturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;

    @PostMapping
    public ResponseEntity<Factura> generar(@Valid @RequestBody FacturaRequest request) {
        return ResponseEntity.ok(facturaService.generarFactura(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.obtenerFactura(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<Factura> obtenerPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(facturaService.obtenerPorPedido(pedidoId));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Factura>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(facturaService.listarPorCliente(clienteId));
    }

    @PatchMapping("/{id}/emitir")
    public ResponseEntity<Factura> emitir(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.emitirFactura(id));
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<Factura> marcarPagada(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.marcarComoPagada(id));
    }
}
