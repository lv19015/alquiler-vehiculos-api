package org.ues.poo135.alquiler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ues.poo135.alquiler.model.Vehiculo;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;

import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    List<Vehiculo> findByEstado(EstadoVehiculo estado);

    boolean existsByPlaca(String placa);
}