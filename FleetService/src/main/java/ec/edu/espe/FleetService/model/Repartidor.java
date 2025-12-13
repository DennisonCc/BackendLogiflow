package ec.edu.espe.FleetService.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "repartidores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Repartidor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String nombre;

    @Column
    private String cedula;

    @Column
    private String telefono;

    @Column
    private String licencia;

    @OneToOne
    @JoinColumn(name = "vehiculo_id")
    private Vehiculo vehiculo;
}
