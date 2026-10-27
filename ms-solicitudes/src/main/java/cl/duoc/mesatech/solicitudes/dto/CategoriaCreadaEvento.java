package cl.duoc.mesatech.solicitudes.dto;

/** Mismo contrato que publica ms-catalogo (ver docs/contrato-evento.md). */
public record CategoriaCreadaEvento(
        String eventId,
        String tipoEvento,
        String fechaEvento,
        Long categoriaId,
        String nombre
) {
}
