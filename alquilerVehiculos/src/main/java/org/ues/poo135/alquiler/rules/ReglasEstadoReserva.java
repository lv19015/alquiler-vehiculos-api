package org.ues.poo135.alquiler.rules;

import org.ues.poo135.alquiler.exception.ConflictException;
import org.ues.poo135.alquiler.model.Reserva.EstadoReserva;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Ciclo de vida de la reserva.
 *
 *   ACTIVA     -> FINALIZADA, CANCELADA
 *   FINALIZADA -> (estado final)
 *   CANCELADA  -> (estado final)
 *
 * Solo una reserva ACTIVA se puede modificar; solo una reserva cerrada se puede eliminar.
 */
public final class ReglasEstadoReserva {

    private static final Map<EstadoReserva, Set<EstadoReserva>> TRANSICIONES =
            new EnumMap<>(EstadoReserva.class);

    static {
        TRANSICIONES.put(EstadoReserva.ACTIVA,
                EnumSet.of(EstadoReserva.FINALIZADA, EstadoReserva.CANCELADA));
        TRANSICIONES.put(EstadoReserva.FINALIZADA, EnumSet.noneOf(EstadoReserva.class));
        TRANSICIONES.put(EstadoReserva.CANCELADA, EnumSet.noneOf(EstadoReserva.class));
    }

    private ReglasEstadoReserva() {
    }

    public static Set<EstadoReserva> permitidosDesde(EstadoReserva actual) {
        return TRANSICIONES.getOrDefault(actual, EnumSet.noneOf(EstadoReserva.class));
    }

    public static void validarTransicion(EstadoReserva actual, EstadoReserva nuevo) {
        Set<EstadoReserva> permitidos = permitidosDesde(actual);
        if (permitidos.contains(nuevo)) {
            return;
        }
        if (permitidos.isEmpty()) {
            throw new ConflictException(
                    "La reserva ya esta " + actual + " y no admite mas cambios de estado.");
        }
        throw new ConflictException(
                "Transicion de estado no permitida: " + actual + " -> " + nuevo + ".");
    }

    public static void validarModificable(EstadoReserva estado) {
        if (estado != EstadoReserva.ACTIVA) {
            throw new ConflictException(
                    "Solo se pueden modificar reservas ACTIVAS. Estado actual: " + estado + ".");
        }
    }

    public static void validarEliminable(EstadoReserva estado) {
        if (estado == EstadoReserva.ACTIVA) {
            throw new ConflictException(
                    "No se puede eliminar una reserva ACTIVA. Cancelela o finalicela primero.");
        }
    }
}
