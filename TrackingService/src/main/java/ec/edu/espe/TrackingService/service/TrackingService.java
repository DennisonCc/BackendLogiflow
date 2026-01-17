package ec.edu.espe.TrackingService.service;

import ec.edu.espe.TrackingService.dto.UbicacionRequestDTO;
import ec.edu.espe.TrackingService.dto.UbicacionResponseDTO;
import ec.edu.espe.TrackingService.event.RepartidorUbicacionActualizadaEvent;
import ec.edu.espe.TrackingService.messaging.TrackingEventPublisher;
import ec.edu.espe.TrackingService.model.UbicacionRepartidor;
import ec.edu.espe.TrackingService.repository.UbicacionRepartidorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    private final UbicacionRepartidorRepository ubicacionRepository;
    private final TrackingEventPublisher eventPublisher;

    @Transactional
    public UbicacionResponseDTO registrarUbicacion(UbicacionRequestDTO request) {
        log.info("Registrando ubicación para repartidor: {}", request.getRepartidorId());

        UbicacionRepartidor ubicacion = UbicacionRepartidor.builder()
                .repartidorId(request.getRepartidorId())
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .timestamp(LocalDateTime.now())
                .pedidoIdActual(request.getPedidoIdActual())
                .estado(request.getEstado() != null ? request.getEstado() : "DISPONIBLE")
                .velocidad(request.getVelocidad())
                .precision(request.getPrecision())
                .build();

        ubicacion = ubicacionRepository.save(ubicacion);

        // Publicar evento a RabbitMQ
        RepartidorUbicacionActualizadaEvent event = RepartidorUbicacionActualizadaEvent.from(
                ubicacion.getRepartidorId(),
                ubicacion.getLatitud(),
                ubicacion.getLongitud(),
                ubicacion.getTimestamp(),
                ubicacion.getPedidoIdActual(),
                ubicacion.getEstado(),
                ubicacion.getVelocidad(),
                ubicacion.getPrecision()
        );

        eventPublisher.publishUbicacionActualizada(event);

        log.info("Ubicación registrada exitosamente - id: {}, repartidorId: {}",
                ubicacion.getId(), ubicacion.getRepartidorId());

        return mapToResponseDTO(ubicacion);
    }

    public UbicacionResponseDTO obtenerUltimaUbicacion(Long repartidorId) {
        return ubicacionRepository.findUltimaUbicacionByRepartidorId(repartidorId)
                .map(this::mapToResponseDTO)
                .orElse(null);
    }

    public List<UbicacionResponseDTO> obtenerHistorial(Long repartidorId, LocalDateTime inicio, LocalDateTime fin) {
        return ubicacionRepository.findByRepartidorIdAndTimestampBetweenOrderByTimestampDesc(
                        repartidorId, inicio, fin)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<Long> obtenerRepartidoresActivos(String estado, int minutosAtras) {
        LocalDateTime desde = LocalDateTime.now().minusMinutes(minutosAtras);
        return ubicacionRepository.findRepartidoresActivosByEstado(estado, desde);
    }

    private UbicacionResponseDTO mapToResponseDTO(UbicacionRepartidor ubicacion) {
        return UbicacionResponseDTO.builder()
                .id(ubicacion.getId())
                .repartidorId(ubicacion.getRepartidorId())
                .latitud(ubicacion.getLatitud())
                .longitud(ubicacion.getLongitud())
                .timestamp(ubicacion.getTimestamp())
                .pedidoIdActual(ubicacion.getPedidoIdActual())
                .estado(ubicacion.getEstado())
                .velocidad(ubicacion.getVelocidad())
                .precision(ubicacion.getPrecision())
                .build();
    }
}
