package ec.edu.espe.GraphQLService.client;

import ec.edu.espe.GraphQLService.model.FlotaResumen;
import ec.edu.espe.GraphQLService.model.Repartidor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FleetServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.fleet.url}")
    private String fleetServiceUrl;

    public List<Repartidor> getRepartidoresActivos(Long zonaId) {
        try {
            String url = fleetServiceUrl + "/repartidores/activos";
            if (zonaId != null) {
                url += "?zonaId=" + zonaId;
            }
            log.info("Fetching repartidores activos from: {}", url);
            
            List<Repartidor> repartidores = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Repartidor>>() {}
            ).getBody();
            
            return repartidores != null ? repartidores : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching repartidores activos: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public FlotaResumen getFlotaResumen() {
        try {
            String url = fleetServiceUrl + "/resumen";
            log.info("Fetching flota resumen from: {}", url);
            
            FlotaResumen resumen = restTemplate.getForObject(url, FlotaResumen.class);
            return resumen != null ? resumen : FlotaResumen.builder().build();
        } catch (Exception e) {
            log.error("Error fetching flota resumen: {}", e.getMessage());
            // Retornar resumen vacío con valores default
            return FlotaResumen.builder()
                    .totalRepartidores(0)
                    .repartidoresActivos(0)
                    .repartidoresDisponibles(0)
                    .repartidoresEnRuta(0)
                    .vehiculosActivos(0)
                    .pedidosEnCurso(0)
                    .build();
        }
    }
}
