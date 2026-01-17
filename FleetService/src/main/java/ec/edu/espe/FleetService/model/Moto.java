package ec.edu.espe.FleetService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "motos")
@DiscriminatorValue("MOTO")
@PrimaryKeyJoinColumn(name = "vehiculo_id") // La llave foránea que une con la tabla padre
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Moto extends Vehiculo {

    @Column
    private String cilindraje;
    private Boolean tieneMaletero;
}
