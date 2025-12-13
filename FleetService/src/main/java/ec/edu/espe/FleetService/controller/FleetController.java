package ec.edu.espe.FleetService.controller;

import ec.edu.espe.FleetService.dto.mapper.VehiculoMapper;
import ec.edu.espe.FleetService.dto.request.RepartidorRequest;
import ec.edu.espe.FleetService.dto.request.VehiculoRequest;
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

    @PostMapping("/repartidores")
    public ResponseEntity<Repartidor> crearRepartidor(@Valid @RequestBody RepartidorRequest request) {
        return ResponseEntity.ok(fleetService.registrarRepartidor(request));
    }

    @GetMapping("/repartidores")
    public ResponseEntity<List<Repartidor>> listarRepartidores() {
        return ResponseEntity.ok(fleetService.listarRepartidores());
    }
}
