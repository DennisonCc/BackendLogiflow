package ec.edu.espe.TrackingService.controller;

import ec.edu.espe.TrackingService.dto.UbicacionRequestDTO;
import ec.edu.espe.TrackingService.dto.UbicacionResponseDTO;
import ec.edu.espe.TrackingService.service.TrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
@Slf4j
public class TrackingController {

    private final TrackingService trackingService;

    @PostMapping("/ubicacion")
    public ResponseEntity<UbicacionResponseDTO> registrarUbicacion(@Valid @RequestBody UbicacionRequestDTO request) {
        log.info("POST /api/tracking/ubicacion - repartidorId: {}", request.getRepartidorId());
        UbicacionResponseDTO response = trackingService.registrarUbicacion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/repartidor/{repartidorId}/ultima")
    public ResponseEntity<UbicacionResponseDTO> obtenerUltimaUbicacion(@PathVariable Long repartidorId) {
        log.info("GET /api/tracking/repartidor/{}/ultima", repartidorId);
        UbicacionResponseDTO ubicacion = trackingService.obtenerUltimaUbicacion(repartidorId);
        return ubicacion != null
                ? ResponseEntity.ok(ubicacion)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/repartidor/{repartidorId}/historial")
    public ResponseEntity<List<UbicacionResponseDTO>> obtenerHistorial(
            @PathVariable Long repartidorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/tracking/repartidor/{}/historial - inicio: {}, fin: {}",
                repartidorId, inicio, fin);
        List<UbicacionResponseDTO> historial = trackingService.obtenerHistorial(repartidorId, inicio, fin);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/repartidores/activos")
    public ResponseEntity<List<Long>> obtenerRepartidoresActivos(
            @RequestParam(defaultValue = "EN_RUTA") String estado,
            @RequestParam(defaultValue = "10") int minutosAtras) {
        log.info("GET /api/tracking/repartidores/activos - estado: {}, minutosAtras: {}",
                estado, minutosAtras);
        List<Long> repartidores = trackingService.obtenerRepartidoresActivos(estado, minutosAtras);
        return ResponseEntity.ok(repartidores);
    }
}
