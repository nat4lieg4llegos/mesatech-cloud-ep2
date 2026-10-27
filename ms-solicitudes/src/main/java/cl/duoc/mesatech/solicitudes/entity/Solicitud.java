package cl.duoc.mesatech.solicitudes.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "solicitud")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    // Referencia a la categoría de ORIGEN (categoriaId), validada contra la copia local
    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    @Column(name = "categoria_nombre", nullable = false, length = 100)
    private String categoriaNombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitud estado;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;

    @PrePersist
    void alCrear() {
        this.fechaCreacion = Instant.now();
        this.fechaActualizacion = this.fechaCreacion;
        if (this.estado == null) this.estado = EstadoSolicitud.ABIERTA;
    }

    @PreUpdate
    void alActualizar() {
        this.fechaActualizacion = Instant.now();
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
    public Instant getFechaCreacion() { return fechaCreacion; }
    public Instant getFechaActualizacion() { return fechaActualizacion; }
}
