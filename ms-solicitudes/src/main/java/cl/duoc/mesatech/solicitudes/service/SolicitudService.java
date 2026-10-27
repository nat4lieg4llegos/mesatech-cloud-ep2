package cl.duoc.mesatech.solicitudes.service;

import cl.duoc.mesatech.solicitudes.dto.SolicitudRequest;
import cl.duoc.mesatech.solicitudes.entity.CategoriaLocal;
import cl.duoc.mesatech.solicitudes.entity.EstadoSolicitud;
import cl.duoc.mesatech.solicitudes.entity.Solicitud;
import cl.duoc.mesatech.solicitudes.exception.ReglaNegocioException;
import cl.duoc.mesatech.solicitudes.exception.NoEncontradoException;
import cl.duoc.mesatech.solicitudes.repository.CategoriaLocalRepository;
import cl.duoc.mesatech.solicitudes.repository.SolicitudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SolicitudService {

    private static final Logger log = LoggerFactory.getLogger(SolicitudService.class);

    private final SolicitudRepository solicitudes;
    private final CategoriaLocalRepository categorias;

    public SolicitudService(SolicitudRepository solicitudes, CategoriaLocalRepository categorias) {
        this.solicitudes = solicitudes;
        this.categorias = categorias;
    }

    /** Solo se aceptan categorías que YA llegaron por RabbitMQ (copia local). Nunca se consulta Catálogo. */
    @Transactional
    public Solicitud crear(SolicitudRequest request) {
        CategoriaLocal categoria = categorias.findByCategoriaId(request.categoriaId())
                .orElseThrow(() -> new ReglaNegocioException(
                        "La categoría " + request.categoriaId() + " aún no está disponible en Solicitudes. "
                                + "Si se acaba de crear, espere unos segundos e intente nuevamente."));

        Solicitud s = new Solicitud();
        s.setTitulo(request.titulo().trim());
        s.setDescripcion(request.descripcion());
        s.setCategoriaId(categoria.getCategoriaId());
        s.setCategoriaNombre(categoria.getNombre());
        Solicitud guardada = solicitudes.save(s);
        log.info("Solicitud creada: id={} categoriaId={}", guardada.getId(), guardada.getCategoriaId());
        return guardada;
    }

    @Transactional
    public Solicitud cambiarEstado(Long id, EstadoSolicitud nuevo) {
        Solicitud s = obtener(id);
        if (!s.getEstado().puedeCambiarA(nuevo)) {
            throw new ReglaNegocioException("No se puede pasar de " + s.getEstado() + " a " + nuevo
                    + ". Una solicitud solo se resuelve si está EN_PROCESO.");
        }
        s.setEstado(nuevo);
        return solicitudes.save(s);
    }

    @Transactional(readOnly = true)
    public Solicitud obtener(Long id) {
        return solicitudes.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe la solicitud " + id));
    }

    @Transactional(readOnly = true)
    public List<Solicitud> listar() {
        return solicitudes.findAll();
    }
}
