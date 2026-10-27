package cl.duoc.mesatech.catalogo.config;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * El exchange, la cola y el binding NO se declaran aquí: se crean en el broker
 * (archivo de definiciones / panel). Si el exchange no existe, la publicación falla
 * y se responde con error controlado: así se nota el problema en vez de esconderlo.
 */
@Configuration
public class RabbitMQConfig {

    // Envía el evento como JSON (content-type application/json)
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
