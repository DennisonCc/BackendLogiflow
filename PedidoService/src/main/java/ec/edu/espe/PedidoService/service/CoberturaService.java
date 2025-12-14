package ec.edu.espe.PedidoService.service;

import ec.edu.espe.PedidoService.model.TipoEntrega;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CoberturaService {

    // Provincias con cobertura para envíos interprovinciales
    private static final List<String> PROVINCIAS_DISPONIBLES = Arrays.asList(
            "PICHINCHA", "GUAYAS", "AZUAY", "TUNGURAHUA", "IMBABURA", 
            "MANABI", "EL ORO", "SANTO DOMINGO", "COTOPAXI"
    );

    // Cantones/ciudades con cobertura municipal
    private static final List<String> CANTONES_DISPONIBLES = Arrays.asList(
            "QUITO", "GUAYAQUIL", "CUENCA", "AMBATO", "IBARRA",
            "MANTA", "MACHALA", "SANTO DOMINGO", "LATACUNGA"
    );

    public void validarCobertura(String direccion, TipoEntrega tipoEntrega) {
        String direccionUpper = direccion.toUpperCase();
        
        switch (tipoEntrega) {
            case Urbana:
                validarCoberturaUrbana(direccionUpper);
                break;
            case Municipal:
                validarCoberturaMunicipal(direccionUpper);
                break;
            case Interprovincial:
                validarCoberturaInterprovincial(direccionUpper);
                break;
            default:
                throw new RuntimeException("Tipo de entrega no válido");
        }
    }

    private void validarCoberturaUrbana(String direccion) {
        // Para entregas urbanas, se requiere que la dirección contenga "QUITO" o esté en una zona urbana específica
        if (!direccion.contains("QUITO")) {
            throw new RuntimeException("Las entregas urbanas solo están disponibles dentro de Quito");
        }
    }

    private void validarCoberturaMunicipal(String direccion) {
        boolean tieneCobertura = CANTONES_DISPONIBLES.stream()
                .anyMatch(direccion::contains);
        
        if (!tieneCobertura) {
            throw new RuntimeException("No hay cobertura municipal para la dirección especificada. " +
                    "Cantones disponibles: " + String.join(", ", CANTONES_DISPONIBLES));
        }
    }

    private void validarCoberturaInterprovincial(String direccion) {
        boolean tieneCobertura = PROVINCIAS_DISPONIBLES.stream()
                .anyMatch(direccion::contains);
        
        if (!tieneCobertura) {
            throw new RuntimeException("No hay cobertura interprovincial para la dirección especificada. " +
                    "Provincias disponibles: " + String.join(", ", PROVINCIAS_DISPONIBLES));
        }
    }
}
