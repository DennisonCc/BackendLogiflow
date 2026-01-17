package ec.edu.espe.FleetService.service.impl;

import ec.edu.espe.FleetService.dto.mapper.VehiculoMapper;
import ec.edu.espe.FleetService.dto.request.RepartidorRequest;
import ec.edu.espe.FleetService.dto.request.VehiculoRequest;
import ec.edu.espe.FleetService.dto.response.FlotaResumenDto;
import ec.edu.espe.FleetService.model.EstadoVehiculo;
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
    @Transactional(readOnly = true)
    public List<Vehiculo> listarVehiculos() { 
        return vehiculoRepository.findAll(); 
    }
    
    @Override
    @Transactional(readOnly = true)
    public Vehiculo obtenerVehiculo(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con id: " + id));
    }
    
    @Override
    @Transactional
    public Vehiculo actualizarEstadoVehiculo(Long id, EstadoVehiculo estado) {
        Vehiculo vehiculo = obtenerVehiculo(id);
        vehiculo.setEstado(estado);
        return vehiculoRepository.save(vehiculo);
    }
    
    @Override
    @Transactional
    public void eliminarVehiculo(Long id) {
        Vehiculo vehiculo = obtenerVehiculo(id);
        vehiculoRepository.delete(vehiculo);
    }

    @Override
    @Transactional
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
    @Transactional(readOnly = true)
    public List<Repartidor> listarRepartidores() { 
        return repartidorRepository.findAll(); 
    }
    
    @Override
    @Transactional(readOnly = true)
    public Repartidor obtenerRepartidor(Long id) {
        return repartidorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado con id: " + id));
    }
    
    @Override
    @Transactional
    public Repartidor actualizarRepartidor(Long id, RepartidorRequest request) {
        Repartidor repartidor = obtenerRepartidor(id);
        
        repartidor.setNombre(request.getNombre());
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
    @Transactional
    public void eliminarRepartidor(Long id) {
        Repartidor repartidor = obtenerRepartidor(id);
        repartidorRepository.delete(repartidor);
    }

    @Override
    @Transactional(readOnly = true)
    public FlotaResumenDto obtenerResumenFlota() {
        long totalRepartidores = repartidorRepository.count();
        long vehiculosActivos = vehiculoRepository.countByEstado(EstadoVehiculo.Disponible);
        
        // Contar repartidores por estado (simulado por ahora)
        List<Repartidor> repartidores = repartidorRepository.findAll();
        long repartidoresActivos = repartidores.stream()
                .filter(r -> r.getVehiculo() != null && r.getVehiculo().getEstado() == EstadoVehiculo.Disponible)
                .count();
        
        return FlotaResumenDto.builder()
                .totalRepartidores((int) totalRepartidores)
                .repartidoresActivos((int) repartidoresActivos)
                .repartidoresDisponibles((int) repartidoresActivos) // Por ahora igual a activos
                .repartidoresEnRuta(0) // Requiere integración con pedidos
                .vehiculosActivos((int) vehiculosActivos)
                .pedidosEnCurso(0) // Requiere integración con pedidos
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Repartidor> obtenerRepartidoresActivos(Long zonaId) {
        // Por ahora retorna todos los repartidores con vehículos activos
        // En el futuro se puede filtrar por zonaId
        List<Repartidor> todos = repartidorRepository.findAll();
        return todos.stream()
                .filter(r -> r.getVehiculo() != null && r.getVehiculo().getEstado() == EstadoVehiculo.Disponible)
                .toList();
    }
}
