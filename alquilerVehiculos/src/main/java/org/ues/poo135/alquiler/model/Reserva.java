package org.ues.poo135.alquiler.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Un cliente puede tener muchas reservas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Un vehiculo puede tener muchas reservas (en fechas distintas)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Double costoTotal; // dias * precioPorDia del vehiculo

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;

    public enum EstadoReserva {
        ACTIVA,
        FINALIZADA,
        CANCELADA
    }

    /**
     * Calcula el costo total validando fechas, vehiculo y precio.
     * Una reserva del dia 10 al 12 = 2 dias.
     */
    public void calcularCostoTotal() {
        if (fechaInicio == null || fechaFin == null || vehiculo == null) {
            throw new IllegalStateException(
                "El vehiculo y las fechas son obligatorios para calcular el costo."
            );
        }

        if (vehiculo.getPrecioPorDia() == null || vehiculo.getPrecioPorDia() <= 0) {
            throw new IllegalStateException(
                "El precio por dia del vehiculo debe ser positivo."
            );
        }

        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin);

        if (dias <= 0) {
            throw new IllegalArgumentException(
                "La fecha de fin debe ser posterior a la fecha de inicio."
            );
        }

        this.costoTotal = dias * vehiculo.getPrecioPorDia();
    }
}
