package cl.duoc.mesatech.solicitudes.entity;

/**
 * Regla de la EP1: ABIERTA -> EN_PROCESO -> RESUELTA.
 * No se puede resolver una solicitud que no pasó por EN_PROCESO.
 */
public enum EstadoSolicitud {
    ABIERTA,
    EN_PROCESO,
    RESUELTA;

    public boolean puedeCambiarA(EstadoSolicitud nuevo) {
        return switch (this) {
            case ABIERTA -> nuevo == EN_PROCESO;
            case EN_PROCESO -> nuevo == RESUELTA;
            case RESUELTA -> false;
        };
    }
}
