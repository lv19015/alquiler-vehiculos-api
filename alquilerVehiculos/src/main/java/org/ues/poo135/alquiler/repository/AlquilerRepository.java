package org.ues.poo135.alquiler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ues.poo135.alquiler.model.Alquiler;
import org.ues.poo135.alquiler.model.Alquiler.EstadoAlquiler;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {

    @Query("SELECT a FROM Alquiler a JOIN FETCH a.cliente JOIN FETCH a.vehiculo WHERE a.cliente.id = :clienteId ORDER BY a.fechaInicio DESC")
    List<Alquiler> findByClienteIdConDetalles(@Param("clienteId") Long clienteId);

    @Query("SELECT a FROM Alquiler a JOIN FETCH a.cliente JOIN FETCH a.vehiculo WHERE a.vehiculo.id = :vehiculoId ORDER BY a.fechaInicio DESC")
    List<Alquiler> findByVehiculoIdConDetalles(@Param("vehiculoId") Long vehiculoId);

    List<Alquiler> findByClienteId(Long clienteId);

    List<Alquiler> findByVehiculoId(Long vehiculoId);

    boolean existsByVehiculoIdAndEstado(Long vehiculoId, EstadoAlquiler estado);

    @Query("""
        SELECT COUNT(a) > 0 FROM Alquiler a
        WHERE a.vehiculo.id = :vehiculoId
          AND a.estado = 'ACTIVO'
          AND (a.fechaInicio <= :fechaFin AND a.fechaFin >= :fechaInicio)
    """)
    boolean existeConflictoDeFechas(
            @Param("vehiculoId") Long vehiculoId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}