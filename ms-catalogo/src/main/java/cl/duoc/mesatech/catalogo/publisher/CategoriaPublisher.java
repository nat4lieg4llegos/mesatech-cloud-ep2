package cl.duoc.mesatech.catalogo.publisher;

import cl.duoc.mesatech.catalogo.dto.CategoriaCreadaEvento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Publica el evento y ESPERA la confirmación del broker (publisher confirm).
 * - ack=true  -> RabbitMQ aceptó el mensaje (NO significa que el consumidor ya lo guardó).
 * - ack=false o timeout -> no hay garantía de que el mensaje esté en el broker.
 * - returned  -> el broker lo recibió pero no había cola enlazada a la routing key.
 */
@Component
public class CategoriaPublisher {

    private static final Logger log = LoggerFactory.getLogger(CategoriaPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public CategoriaPublisher(RabbitTemplate rabbitTemplate,
                              @Value("${app.rabbitmq.exchange}") String exchange,
                              @Value("${app.rabbitmq.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publicar(CategoriaCreadaEvento evento) {
        CorrelationData correlation = new CorrelationData(evento.eventId());

        rabbitTemplate.convertAndSend(exchange, routingKey, evento, message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            message.getMessageProperties().setMessageId(evento.eventId());
            message.getMessageProperties().setType(evento.tipoEvento());
            return message;
        }, correlation);

        try {
            CorrelationData.Confirm confirm = correlation.getFuture().get(10, TimeUnit.SECONDS);

            if (!confirm.ack()) {
                throw new AmqpException("RabbitMQ no confirmó la publicación: " + confirm.reason());
            }
            if (correlation.getReturned() != null) {
                throw new AmqpException("RabbitMQ no encontró una cola para la routing key " + routingKey);
            }
            log.info("Evento publicado y confirmado: eventId={} categoriaId={} exchange={} routingKey={}",
                    evento.eventId(), evento.categoriaId(), exchange, routingKey);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AmqpException("Se interrumpió la espera de confirmación", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new AmqpException("No se pudo confirmar la publicación en RabbitMQ", e);
        }
    }
}
