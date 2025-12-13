package ec.edu.espe.PedidoService.controller;

import ec.edu.espe.PedidoService.dto.EstadoRequest;
import ec.edu.espe.PedidoService.dto.PedidoRequest;
import ec.edu.espe.PedidoService.model.Pedido;
import ec.edu.espe.PedidoService.services.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(pedidoService.crearPedido(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedido(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> cambiarEstado(@PathVariable Long id,
                                                @Valid @RequestBody EstadoRequest request) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.ok("Pedido cancelado exitosamente");
    }
}
