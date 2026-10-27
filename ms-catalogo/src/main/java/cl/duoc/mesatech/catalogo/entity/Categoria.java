package cl.duoc.mesatech.catalogo.entity;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * Categoría de atención original. Vive SOLO en la RDS de Catálogo.
 * Su id es el "categoriaId" que viaja en el evento y que Solicitudes conserva en su copia.
 */
@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @PrePersist
    void alCrear() {
        this.fechaCreacion = Instant.now();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Instant getFechaCreacion() { return fechaCreacion; }
}
