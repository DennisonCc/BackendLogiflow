package ec.edu.espe.BillingService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaBase;

    @Column(precision = 10, scale = 2)
    private BigDecimal recargos = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal descuentos = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado = EstadoFactura.BORRADOR;

    @Column
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @Column
    private LocalDateTime fechaPago;
}
