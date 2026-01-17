package ec.edu.espe.FleetService.controller;

import ec.edu.espe.FleetService.dto.mapper.VehiculoMapper;
import ec.edu.espe.FleetService.dto.request.ActualizarEstadoVehiculoRequest;
import ec.edu.espe.FleetService.dto.request.RepartidorRequest;
import ec.edu.espe.FleetService.dto.request.VehiculoRequest;
import ec.edu.espe.FleetService.dto.response.FlotaResumenDto;
import ec.edu.espe.FleetService.dto.response.VehiculoResponseDto;
import ec.edu.espe.FleetService.model.Repartidor;
import ec.edu.espe.FleetService.model.Vehiculo;
import ec.edu.espe.FleetService.service.FleetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fleet")
@RequiredArgsConstructor
public class FleetController {

    private final FleetService fleetService;
    private final VehiculoMapper vehiculoMapper;

    @PostMapping("/vehiculos")
    public ResponseEntity<VehiculoResponseDto> crearVehiculo(@Valid @RequestBody VehiculoRequest request) {
        Vehiculo vehiculo = fleetService.crearVehiculo(request);
        return ResponseEntity.ok(vehiculoMapper.toDto(vehiculo));
    }

    @GetMapping("/vehiculos")
    public ResponseEntity<List<VehiculoResponseDto>> listarVehiculos() {
        List<Vehiculo> vehiculos = fleetService.listarVehiculos();
        List<VehiculoResponseDto> response = vehiculos.stream()
                .map(vehiculoMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/vehiculos/{id}")
    public ResponseEntity<VehiculoResponseDto> obtenerVehiculo(@PathVariable Long id) {
        Vehiculo vehiculo = fleetService.obtenerVehiculo(id);
        return ResponseEntity.ok(vehiculoMapper.toDto(vehiculo));
    }
    
    @PatchMapping("/vehiculos/{id}/estado")
    public ResponseEntity<VehiculoResponseDto> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoVehiculoRequest request) {
        Vehiculo vehiculo = fleetService.actualizarEstadoVehiculo(id, request.getEstado());
        return ResponseEntity.ok(vehiculoMapper.toDto(vehiculo));
    }
    
    @DeleteMapping("/vehiculos/{id}")
    public ResponseEntity<?> eliminarVehiculo(@PathVariable Long id) {
        fleetService.eliminarVehiculo(id);
        return ResponseEntity.ok("Vehículo eliminado exitosamente");
    }

    @PostMapping("/repartidores")
    public ResponseEntity<Repartidor> crearRepartidor(@Valid @RequestBody RepartidorRequest request) {
        return ResponseEntity.ok(fleetService.registrarRepartidor(request));
    }

    @GetMapping("/repartidores")
    public ResponseEntity<List<Repartidor>> listarRepartidores() {
        return ResponseEntity.ok(fleetService.listarRepartidores());
    }
    
    @GetMapping("/repartidores/{id}")
    public ResponseEntity<Repartidor> obtenerRepartidor(@PathVariable Long id) {
        return ResponseEntity.ok(fleetService.obtenerRepartidor(id));
    }
    
    @PutMapping("/repartidores/{id}")
    public ResponseEntity<Repartidor> actualizarRepartidor(
            @PathVariable Long id,
            @Valid @RequestBody RepartidorRequest request) {
        return ResponseEntity.ok(fleetService.actualizarRepartidor(id, request));
    }
    
    @DeleteMapping("/repartidores/{id}")
    public ResponseEntity<?> eliminarRepartidor(@PathVariable Long id) {
        fleetService.eliminarRepartidor(id);
        return ResponseEntity.ok("Repartidor eliminado exitosamente");
    }

    // Endpoints para GraphQL
    @GetMapping("/resumen")
    public ResponseEntity<FlotaResumenDto> obtenerResumenFlota() {
        return ResponseEntity.ok(fleetService.obtenerResumenFlota());
    }

    @GetMapping("/repartidores/activos")
    public ResponseEntity<List<Repartidor>> obtenerRepartidoresActivos(
            @RequestParam(required = false) Long zonaId) {
        return ResponseEntity.ok(fleetService.obtenerRepartidoresActivos(zonaId));
    }
}
