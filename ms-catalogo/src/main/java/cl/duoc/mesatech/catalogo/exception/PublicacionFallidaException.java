package cl.duoc.mesatech.catalogo.exception;

import cl.duoc.mesatech.catalogo.entity.Categoria;

/** La categoría se guardó en RDS, pero el evento no fue confirmado por RabbitMQ. */
public class PublicacionFallidaException extends RuntimeException {

    private final Categoria categoria;
    private final String eventId;

    public PublicacionFallidaException(Categoria categoria, String eventId, Throwable causa) {
        super(causa.getMessage(), causa);
        this.categoria = categoria;
        this.eventId = eventId;
    }

    public Categoria getCategoria() { return categoria; }
    public String getEventId() { return eventId; }
}
