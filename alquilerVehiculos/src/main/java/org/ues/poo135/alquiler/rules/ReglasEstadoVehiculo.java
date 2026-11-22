package org.ues.poo135.alquiler.rules;

import org.ues.poo135.alquiler.exception.ConflictException;
import org.ues.poo135.alquiler.model.Vehiculo.EstadoVehiculo;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Maquina de estados del vehiculo. Unica fuente de verdad sobre
 * que cambios de estado estan permitidos.
 *
 *   DISPONIBLE       -> ALQUILADO, EN_MANTENIMIENTO
 *   ALQUILADO        -> DISPONIBLE            (al cancelar o finalizar la reserva)
 *   EN_MANTENIMIENTO -> DISPONIBLE            (al terminar el mantenimiento)
 *
 * Un vehiculo alquilado no pasa directo a mantenimiento ni viceversa:
 * primero debe volver a DISPONIBLE.
 */
public final class ReglasEstadoVehiculo {

    private static final Map<EstadoVehiculo, Set<EstadoVehiculo>> TRANSICIONES =
            new EnumMap<>(EstadoVehiculo.class);

    static {
        TRANSICIONES.put(EstadoVehiculo.DISPONIBLE,
                EnumSet.of(EstadoVehiculo.ALQUILADO, EstadoVehiculo.EN_MANTENIMIENTO));
        TRANSICIONES.put(EstadoVehiculo.ALQUILADO,
                EnumSet.of(EstadoVehiculo.DISPONIBLE));
        TRANSICIONES.put(EstadoVehiculo.EN_MANTENIMIENTO,
                EnumSet.of(EstadoVehiculo.DISPONIBLE));
    }

    private ReglasEstadoVehiculo() {
    }

    public static Set<EstadoVehiculo> permitidosDesde(EstadoVehiculo actual) {
        return TRANSICIONES.getOrDefault(actual, EnumSet.noneOf(EstadoVehiculo.class));
    }

    /** Pasar al mismo estado en que ya esta se considera valido (no hace nada). */
    public static boolean puedeTransicionar(EstadoVehiculo actual, EstadoVehiculo nuevo) {
        return actual == nuevo || permitidosDesde(actual).contains(nuevo);
    }

    public static void validarTransicion(EstadoVehiculo actual, EstadoVehiculo nuevo) {
        if (!puedeTransicionar(actual, nuevo)) {
            throw new ConflictException(
                    "Transicion de estado no permitida: " + actual + " -> " + nuevo
                            + ". Estados permitidos desde " + actual + ": " + permitidosDesde(actual) + ".");
        }
    }
}
