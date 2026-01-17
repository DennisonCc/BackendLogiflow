package ec.edu.espe.GraphQLService.client;

import ec.edu.espe.GraphQLService.model.KPIDiario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillingServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.pedido.url}")
    private String pedidoServiceUrl;

    public KPIDiario getKPIDiario(String fecha) {
        try {
            String url = pedidoServiceUrl + "/kpi/diario?fecha=" + fecha;
            log.info("Fetching KPI diario from: {}", url);
            
            KPIDiario kpi = restTemplate.getForObject(url, KPIDiario.class);
            return kpi != null ? kpi : buildDefaultKPI(fecha);
        } catch (Exception e) {
            log.error("Error fetching KPI diario for {}: {}", fecha, e.getMessage());
            return buildDefaultKPI(fecha);
        }
    }

    private KPIDiario buildDefaultKPI(String fecha) {
        return KPIDiario.builder()
                .fecha(fecha)
                .pedidosCreados(0)
                .pedidosCompletados(0)
                .pedidosCancelados(0)
                .tasaCompletado(0.0f)
                .tiempoPromedioEntrega(0)
                .distanciaPromedioKm(0.0f)
                .ingresoTotal(0.0f)
                .build();
    }
}
