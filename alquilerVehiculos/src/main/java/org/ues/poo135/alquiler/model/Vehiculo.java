package org.ues.poo135.alquiler.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehiculos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false, unique = true)
    private String placa;

    @Column(nullable = false)
    private String categoria; // Sedan, SUV, Pickup, etc.

    @Column(nullable = false)
    private Double precioPorDia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVehiculo estado;

    public enum EstadoVehiculo {
        DISPONIBLE,
        ALQUILADO,
        EN_MANTENIMIENTO
    }
}
