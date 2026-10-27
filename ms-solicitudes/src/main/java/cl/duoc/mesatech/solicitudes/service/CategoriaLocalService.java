package cl.duoc.mesatech.solicitudes.service;

import cl.duoc.mesatech.solicitudes.dto.CategoriaCreadaEvento;
import cl.duoc.mesatech.solicitudes.entity.CategoriaLocal;
import cl.duoc.mesatech.solicitudes.repository.CategoriaLocalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaLocalService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaLocalService.class);

    private final CategoriaLocalRepository repository;

    public CategoriaLocalService(CategoriaLocalRepository repository) {
        this.repository = repository;
    }

    /**
     * Guarda la copia. Si llega de nuevo un evento con el mismo categoriaId
     * (reentrega o reenvío), no se duplica: se registra en el log y se termina sin error.
     * Esto NO es una garantía "exactamente una vez": solo evita filas repetidas.
     */
    @Transactional
    public void guardarCopia(CategoriaCreadaEvento evento) {
        if (repository.findByCategoriaId(evento.categoriaId()).isPresent()) {
            log.warn("Categoría ya existía en la copia local, se omite: categoriaId={} eventId={}",
                    evento.categoriaId(), evento.eventId());
            return;
        }
        CategoriaLocal copia = new CategoriaLocal();
        copia.setCategoriaId(evento.categoriaId());
        copia.setNombre(evento.nombre());
        copia.setEventId(evento.eventId());
        copia.setFechaEvento(evento.fechaEvento());
        CategoriaLocal guardada = repository.saveAndFlush(copia);

        log.info("Copia guardada en RDS Solicitudes: categoriaId={} eventId={} idLocal={}",
                guardada.getCategoriaId(), guardada.getEventId(), guardada.getId());
    }

    @Transactional(readOnly = true)
    public List<CategoriaLocal> listar() {
        return repository.findAll();
    }
}
