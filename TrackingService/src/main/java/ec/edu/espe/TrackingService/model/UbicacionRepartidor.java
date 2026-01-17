package ec.edu.espe.TrackingService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ubicaciones_repartidor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UbicacionRepartidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long repartidorId;

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column
    private Long pedidoIdActual;

    @Column(length = 20)
    private String estado; // EN_RUTA, DISPONIBLE, PAUSADO

    @Column
    private Double velocidad; // km/h

    @Column
    private Integer precision; // metros

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
