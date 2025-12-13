package ec.edu.espe.FleetService.service.impl;

import ec.edu.espe.FleetService.dto.mapper.VehiculoMapper;
import ec.edu.espe.FleetService.dto.request.RepartidorRequest;
import ec.edu.espe.FleetService.dto.request.VehiculoRequest;
import ec.edu.espe.FleetService.model.Repartidor;
import ec.edu.espe.FleetService.model.Vehiculo;
import ec.edu.espe.FleetService.repository.RepartidorRepository;
import ec.edu.espe.FleetService.repository.VehiculoRepository;
import ec.edu.espe.FleetService.service.FleetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FleetServiceImpl implements FleetService {
    private final VehiculoRepository vehiculoRepository;
    private final RepartidorRepository repartidorRepository;
    private final VehiculoMapper vehiculoMapper;

    @Override
    @Transactional
    public Vehiculo crearVehiculo(VehiculoRequest request) {
        // Validar existencia
        if (vehiculoRepository.findByPlaca(request.getPlaca()).isPresent()) {
            throw new RuntimeException("Ya existe un vehículo con esa placa");
        }

        // El mapper se encarga de saber si es Moto o Camion
        Vehiculo vehiculo = vehiculoMapper.toEntity(request);

        return vehiculoRepository.save(vehiculo);
    }

    @Override
    public List<Vehiculo> listarVehiculos() { return vehiculoRepository.findAll(); }

    @Override
    public Repartidor registrarRepartidor(RepartidorRequest request) {
        if (repartidorRepository.findByCedula(request.getCedula()).isPresent()) {
            throw new RuntimeException("Ya existe un repartidor con esa cédula");
        }

        Repartidor repartidor = new Repartidor();
        repartidor.setNombre(request.getNombre());
        repartidor.setCedula(request.getCedula());
        repartidor.setTelefono(request.getTelefono());
        repartidor.setLicencia(request.getLicencia());

        if (request.getVehiculoId() != null) {
            Vehiculo vehiculo = vehiculoRepository.findById(request.getVehiculoId())
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
            repartidor.setVehiculo(vehiculo);
        }

        return repartidorRepository.save(repartidor);
    }

    @Override
    public List<Repartidor> listarRepartidores() { return repartidorRepository.findAll(); }
}
