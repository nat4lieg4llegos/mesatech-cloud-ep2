package cl.duoc.mesatech.catalogo.service;

import cl.duoc.mesatech.catalogo.dto.CategoriaCreadaEvento;
import cl.duoc.mesatech.catalogo.dto.CategoriaRequest;
import cl.duoc.mesatech.catalogo.entity.Categoria;
import cl.duoc.mesatech.catalogo.exception.NombreDuplicadoException;
import cl.duoc.mesatech.catalogo.exception.PublicacionFallidaException;
import cl.duoc.mesatech.catalogo.publisher.CategoriaPublisher;
import cl.duoc.mesatech.catalogo.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Paso 1: guardar en RDS Catálogo (se confirma de inmediato).
 * Paso 2: publicar el evento.
 * Son operaciones SEPARADAS: si el paso 2 falla, la categoría YA quedó guardada
 * y Solicitudes no recibe la copia (inconsistencia que se documenta en P7).
 */
@Service
public class CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaRepository repository;
    private final CategoriaPublisher publisher;

    public CategoriaService(CategoriaRepository repository, CategoriaPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Categoria crear(CategoriaRequest request) {
        String nombre = request.nombre().trim();
        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new NombreDuplicadoException(nombre);
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(request.descripcion());
        Categoria guardada = repository.save(categoria);
        log.info("Categoría guardada en RDS Catálogo: categoriaId={} nombre={}", guardada.getId(), guardada.getNombre());

        CategoriaCreadaEvento evento = new CategoriaCreadaEvento(
                UUID.randomUUID().toString(),
                CategoriaCreadaEvento.TIPO,
                Instant.now().toString(),
                guardada.getId(),
                guardada.getNombre());

        try {
            publisher.publicar(evento);
        } catch (AmqpException e) {
            log.error("Fallo de publicación: categoriaId={} eventId={} causa={}",
                    guardada.getId(), evento.eventId(), e.getMessage());
            throw new PublicacionFallidaException(guardada, evento.eventId(), e);
        }
        return guardada;
    }

    public List<Categoria> listar() {
        return repository.findAll();
    }
}
