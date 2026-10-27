package cl.duoc.mesatech.solicitudes.entity;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * COPIA local de una categoría recibida por RabbitMQ. Vive en la RDS de Solicitudes.
 * categoriaId conserva el id de origen (RDS Catálogo); el id propio es solo técnico.
 */
@Entity
@Table(name = "categoria_local")
public class CategoriaLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "categoria_id", nullable = false, unique = true)
    private Long categoriaId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "event_id", nullable = false, length = 36)
    private String eventId;

    @Column(name = "fecha_evento", nullable = false, length = 40)
    private String fechaEvento;

    @Column(name = "fecha_recepcion", nullable = false)
    private Instant fechaRecepcion;

    @PrePersist
    void alRecibir() {
        this.fechaRecepcion = Instant.now();
    }

    public Long getId() { return id; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(String fechaEvento) { this.fechaEvento = fechaEvento; }
    public Instant getFechaRecepcion() { return fechaRecepcion; }
}
