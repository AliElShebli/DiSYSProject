package at.uastw.energy.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE        = "energy.exchange";
    public static final String QUEUE_PRODUCER  = "energy.queue.producer";
    public static final String QUEUE_USER      = "energy.queue.user";
    public static final String QUEUE_USAGE     = "energy.queue.usage";
    public static final String QUEUE_PERCENT   = "energy.queue.percent";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue producerQueue() {
        return new Queue(QUEUE_PRODUCER, true);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(QUEUE_USER, true);
    }

    @Bean
    public Queue usageQueue() {
        return new Queue(QUEUE_USAGE, true);
    }

    @Bean
    public Queue percentQueue() {
        return new Queue(QUEUE_PERCENT, true);
    }

    @Bean
    public Binding bindProducer(TopicExchange ex) {
        return BindingBuilder.bind(producerQueue()).to(ex).with("energy.producer");
    }

    @Bean
    public Binding bindUser(TopicExchange ex) {
        return BindingBuilder.bind(userQueue()).to(ex).with("energy.user");
    }

    @Bean
    public Binding bindUsage(TopicExchange ex) {
        return BindingBuilder.bind(usageQueue()).to(ex).with("energy.usage");
    }

    @Bean
    public Binding bindPercent(TopicExchange ex) {
        return BindingBuilder.bind(percentQueue()).to(ex).with("energy.percent");
    }

    // ---------------------------------------------------------
    // ObjectMapper bean (with JavaTimeModule) so that
    // Jackson2JsonMessageConverter can handle LocalDateTime, etc.
    // ---------------------------------------------------------
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    // ---------------------------------------------------------
    // JSON MessageConverter using our ObjectMapper
    // ---------------------------------------------------------
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

    // ---------------------------------------------------------
    // RabbitTemplate configured to use JSON converter and default exchange
    // ---------------------------------------------------------
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        template.setExchange(EXCHANGE);
        return template;
    }
}
