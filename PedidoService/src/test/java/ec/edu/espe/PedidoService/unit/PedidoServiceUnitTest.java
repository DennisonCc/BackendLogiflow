package ec.edu.espe.PedidoService.unit;

import ec.edu.espe.PedidoService.dto.PedidoRequest;
import ec.edu.espe.PedidoService.model.EstadoPedido;
import ec.edu.espe.PedidoService.model.Pedido;
import ec.edu.espe.PedidoService.model.TipoEntrega;
import ec.edu.espe.PedidoService.repository.PedidoRepository;
import ec.edu.espe.PedidoService.service.CoberturaService;
import ec.edu.espe.PedidoService.services.impl.PedidoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para PedidoServiceImpl
 * Valida la lógica de negocio sin depender de la base de datos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - PedidoService")
class PedidoServiceUnitTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CoberturaService coberturaService;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private PedidoRequest pedidoRequestValido;

    @BeforeEach
    void setUp() {
        pedidoRequestValido = new PedidoRequest();
        pedidoRequestValido.setClienteId(1L);
        pedidoRequestValido.setDireccionOrigen("Quito Centro");
        pedidoRequestValido.setDireccionDestino("Cumbayá");
        pedidoRequestValido.setTipoEntrega(TipoEntrega.Urbana);
        pedidoRequestValido.setDescripcionPaquete("Paquete pequeño");
    }

    @Test
    @DisplayName("Test 1: Debe crear pedido con tipo de entrega Urbana")
    void debeCrearPedidoConTipoUrbana() {
        // Arrange
        Pedido pedidoEsperado = new Pedido();
        pedidoEsperado.setId(1L);
        pedidoEsperado.setClienteId(1L);
        pedidoEsperado.setTipoEntrega(TipoEntrega.Urbana);
        pedidoEsperado.setEstado(EstadoPedido.Recibido);

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoEsperado);
        doNothing().when(coberturaService).validarCobertura(anyString(), any(TipoEntrega.class));

        // Act
        Pedido resultado = pedidoService.crearPedido(pedidoRequestValido);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTipoEntrega()).isEqualTo(TipoEntrega.Urbana);
        assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.Recibido);
        verify(coberturaService, times(1)).validarCobertura("Cumbayá", TipoEntrega.Urbana);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Test 2: Debe crear pedido con tipo de entrega Municipal")
    void debeCrearPedidoConTipoMunicipal() {
        // Arrange
        pedidoRequestValido.setTipoEntrega(TipoEntrega.Municipal);
        pedidoRequestValido.setDireccionDestino("Valle de los Chillos");

        Pedido pedidoEsperado = new Pedido();
        pedidoEsperado.setId(2L);
        pedidoEsperado.setTipoEntrega(TipoEntrega.Municipal);
        pedidoEsperado.setEstado(EstadoPedido.Recibido);

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoEsperado);
        doNothing().when(coberturaService).validarCobertura(anyString(), any(TipoEntrega.class));

        // Act
        Pedido resultado = pedidoService.crearPedido(pedidoRequestValido);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTipoEntrega()).isEqualTo(TipoEntrega.Municipal);
        verify(coberturaService, times(1)).validarCobertura("Valle de los Chillos", TipoEntrega.Municipal);
    }

    @Test
    @DisplayName("Test 3: Debe crear pedido con tipo de entrega Interprovincial")
    void debeCrearPedidoConTipoInterprovincial() {
        // Arrange
        pedidoRequestValido.setTipoEntrega(TipoEntrega.Interprovincial);
        pedidoRequestValido.setDireccionDestino("Guayaquil");

        Pedido pedidoEsperado = new Pedido();
        pedidoEsperado.setId(3L);
        pedidoEsperado.setTipoEntrega(TipoEntrega.Interprovincial);
        pedidoEsperado.setEstado(EstadoPedido.Recibido);

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoEsperado);
        doNothing().when(coberturaService).validarCobertura(anyString(), any(TipoEntrega.class));

        // Act
        Pedido resultado = pedidoService.crearPedido(pedidoRequestValido);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTipoEntrega()).isEqualTo(TipoEntrega.Interprovincial);
        verify(coberturaService, times(1)).validarCobertura("Guayaquil", TipoEntrega.Interprovincial);
    }

    @Test
    @DisplayName("Test 4: Debe validar tipo de entrega antes de crear pedido")
    void debeValidarTipoEntregaAntesDeCrear() {
        // Arrange
        doNothing().when(coberturaService).validarCobertura(anyString(), any(TipoEntrega.class));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(new Pedido());

        // Act
        pedidoService.crearPedido(pedidoRequestValido);

        // Assert
        verify(coberturaService, times(1)).validarCobertura(
                pedidoRequestValido.getDireccionDestino(),
                pedidoRequestValido.getTipoEntrega()
        );
    }

    @Test
    @DisplayName("Test 5: Debe lanzar excepción si validación de cobertura falla")
    void debeLanzarExcepcionSiCoberturaInvalida() {
        // Arrange
        doThrow(new RuntimeException("Destino fuera de cobertura"))
                .when(coberturaService).validarCobertura(anyString(), any(TipoEntrega.class));

        // Act & Assert
        assertThatThrownBy(() -> pedidoService.crearPedido(pedidoRequestValido))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Destino fuera de cobertura");

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Test 6: Debe establecer estado inicial como Recibido")
    void debeEstablecerEstadoInicialRecibido() {
        // Arrange
        Pedido pedidoCapturado = new Pedido();
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(1L);
            return pedido;
        });
        doNothing().when(coberturaService).validarCobertura(anyString(), any(TipoEntrega.class));

        // Act
        Pedido resultado = pedidoService.crearPedido(pedidoRequestValido);

        // Assert
        verify(pedidoRepository).save(argThat(pedido ->
                pedido.getEstado() == EstadoPedido.Recibido
        ));
    }

    @Test
    @DisplayName("Test 7: Debe obtener pedido por ID existente")
    void debeObtenerPedidoPorIdExistente() {
        // Arrange
        Pedido pedidoEsperado = new Pedido();
        pedidoEsperado.setId(1L);
        pedidoEsperado.setClienteId(1L);
        pedidoEsperado.setEstado(EstadoPedido.Recibido);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEsperado));

        // Act
        Pedido resultado = pedidoService.obtenerPedido(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test 8: Debe lanzar excepción si pedido no existe")
    void debeLanzarExcepcionSiPedidoNoExiste() {
        // Arrange
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pedidoService.obtenerPedido(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Pedido no encontrado con id: 999");

        verify(pedidoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Test 9: Debe asignar repartidor y cambiar estado a Asignado")
    void debeAsignarRepartidorYCambiarEstado() {
        // Arrange
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(1L);
        pedidoExistente.setEstado(EstadoPedido.Recibido);
        pedidoExistente.setRepartidorId(null);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ec.edu.espe.PedidoService.dto.EstadoRequest estadoRequest = new ec.edu.espe.PedidoService.dto.EstadoRequest();
        estadoRequest.setNuevoEstado(EstadoPedido.Asignado);
        estadoRequest.setRepartidorId(5L);

        // Act
        Pedido resultado = pedidoService.actualizarEstado(1L, estadoRequest);

        // Assert
        assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.Asignado);
        assertThat(resultado.getRepartidorId()).isEqualTo(5L);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Test 10: Debe cancelar pedido correctamente")
    void debeCancelarPedidoCorrectamente() {
        // Arrange
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(1L);
        pedidoExistente.setEstado(EstadoPedido.Recibido);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        pedidoService.cancelarPedido(1L);

        // Assert
        verify(pedidoRepository).save(argThat(pedido ->
                pedido.getEstado() == EstadoPedido.Cancelado
        ));
    }

    @Test
    @DisplayName("Test 11: Debe preservar todos los datos al crear pedido")
    void debePreservarTodosDatosAlCrear() {
        // Arrange
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(1L);
            return pedido;
        });
        doNothing().when(coberturaService).validarCobertura(anyString(), any(TipoEntrega.class));

        // Act
        pedidoService.crearPedido(pedidoRequestValido);

        // Assert
        verify(pedidoRepository).save(argThat(pedido ->
                pedido.getClienteId().equals(1L) &&
                pedido.getDireccionOrigen().equals("Quito Centro") &&
                pedido.getDireccionDestino().equals("Cumbayá") &&
                pedido.getTipoEntrega() == TipoEntrega.Urbana &&
                pedido.getDescripcionPaquete().equals("Paquete pequeño")
        ));
    }
}
