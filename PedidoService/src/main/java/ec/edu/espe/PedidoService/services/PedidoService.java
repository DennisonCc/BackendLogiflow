package ec.edu.espe.PedidoService.services;

import ec.edu.espe.PedidoService.dto.EstadoRequest;
import ec.edu.espe.PedidoService.dto.PedidoRequest;
import ec.edu.espe.PedidoService.model.Pedido;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;


public interface PedidoService {
    Pedido crearPedido(PedidoRequest pedido);
    Pedido obtenerPedido(Long id);
    List<Pedido> listarPorCliente(Long clienteId);
    Pedido actualizarEstado(Long id, EstadoRequest request);
    void cancelarPedido(Long id);

}
