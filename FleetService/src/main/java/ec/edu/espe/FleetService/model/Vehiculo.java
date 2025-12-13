package ec.edu.espe.FleetService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "vehiculos")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_vehiculo", discriminatorType = DiscriminatorType.STRING)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String placa;

    @Column
    private String marca;

    @Column
    private String modelo;

    @Enumerated(EnumType.STRING)
    private EstadoVehiculo estado = EstadoVehiculo.Disponible;
}
