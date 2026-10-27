package cl.duoc.mesatech.catalogo.dto;

/**
 * Contrato del evento publicado en RabbitMQ (exchange catalogo.exchange, routing key categoria.creada).
 *
 * eventId      String  UUID v4 generado por Catálogo; también se usa como messageId de AMQP.
 * tipoEvento   String  Siempre "CATEGORIA_CREADA".
 * fechaEvento  String  Fecha y hora en UTC, formato ISO-8601 (ej: 2026-11-03T21:15:30.123Z).
 * categoriaId  Long    Id de la categoría en la RDS de Catálogo (referencia de origen).
 * nombre       String  Nombre de la categoría.
 */
public record CategoriaCreadaEvento(
        String eventId,
        String tipoEvento,
        String fechaEvento,
        Long categoriaId,
        String nombre
) {
    public static final String TIPO = "CATEGORIA_CREADA";
}
