package org.ues.poo135.alquiler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ues.poo135.alquiler.model.Reserva;
import org.ues.poo135.alquiler.model.Reserva.EstadoReserva;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByClienteId(Long clienteId);

    List<Reserva> findByVehiculoId(Long vehiculoId);

    // Usados por los modulos de vehiculos y clientes antes de eliminar
    boolean existsByVehiculoId(Long vehiculoId);

    boolean existsByClienteId(Long clienteId);

    boolean existsByVehiculoIdAndEstado(Long vehiculoId, EstadoReserva estado);

    /**
     * Hay solapamiento cuando una reserva existente empieza antes de que
     * termine el rango pedido Y termina despues de que empieza el rango pedido.
     * El parametro excluirId permite ignorar la propia reserva al editarla.
     */
    @Query("""
        SELECT COUNT(r) > 0
        FROM Reserva r
        WHERE r.vehiculo.id = :vehiculoId
          AND r.estado = :estado
          AND r.fechaInicio < :fechaFin
          AND r.fechaFin > :fechaInicio
          AND r.id <> :excluirId
    """)
    boolean existeSolapamiento(
        @Param("vehiculoId") Long vehiculoId,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin,
        @Param("excluirId") Long excluirId,
        @Param("estado") EstadoReserva estado
    );

    /** Solo cuentan las reservas ACTIVAS: las canceladas liberan las fechas. */
    default boolean existeReservaActivaEnRango(
            Long vehiculoId, LocalDate fechaInicio, LocalDate fechaFin, Long excluirId) {
        return existeSolapamiento(vehiculoId, fechaInicio, fechaFin, excluirId, EstadoReserva.ACTIVA);
    }
}
