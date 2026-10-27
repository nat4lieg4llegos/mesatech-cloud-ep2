package cl.duoc.mesatech.solicitudes.listener;

import cl.duoc.mesatech.solicitudes.dto.CategoriaCreadaEvento;
import cl.duoc.mesatech.solicitudes.service.CategoriaLocalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Modo AUTO (como el laboratorio): Spring envía el ACK a RabbitMQ SOLO cuando este método
 * termina sin excepción, es decir, DESPUÉS de guardar en RDS. Si guardarCopia lanza error
 * (ej: RDS caída), la excepción se propaga, no hay ACK y el mensaje vuelve a la cola.
 */
@Component
public class CategoriaListener {

    private static final Logger log = LoggerFactory.getLogger(CategoriaListener.class);

    private final CategoriaLocalService service;

    public CategoriaListener(CategoriaLocalService service) {
        this.service = service;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void recibir(CategoriaCreadaEvento evento) {
        log.info("Evento recibido: eventId={} tipo={} categoriaId={} nombre={}",
                evento.eventId(), evento.tipoEvento(), evento.categoriaId(), evento.nombre());

        // Un mensaje sin los datos mínimos nunca se podrá guardar: se descarta en vez de reintentarlo para siempre
        if (evento.categoriaId() == null || evento.nombre() == null || evento.eventId() == null) {
            throw new AmqpRejectAndDontRequeueException("Evento incompleto, se descarta: " + evento);
        }

        service.guardarCopia(evento);
    }
}
