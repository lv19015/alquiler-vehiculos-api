
package org.ues.poo135.alquiler.repository;

import org.ues.poo135.alquiler.model.Vehiculo;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    List<Vehiculo> findByEstado(EstadoVehiculo estado);

    boolean existsByPlaca(String placa);

    /*
     * TODO: Reactivar cuando se implemente la entidad Reserva
     *
     * Verifica si el vehículo tiene reservas que se solapan
     * con el rango de fechas solicitado
     *
     * @Query("""
     *     SELECT COUNT(r) = 0
     *     FROM Reserva r
     *     WHERE r.vehiculo.id = :vehiculoId
     *       AND r.estado <> 'CANCELADA'
     *       AND r.fechaInicio < :fechaFin
     *       AND r.fechaFin > :fechaInicio
     *     """)
     * boolean estaDisponibleEnFechas(
     *     @Param("vehiculoId") Long vehiculoId,
     *     @Param("fechaInicio") LocalDate fechaInicio,
     *     @Param("fechaFin") LocalDate fechaFin
     * );
     */
}
