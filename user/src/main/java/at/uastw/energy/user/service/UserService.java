package at.uastw.energy.user.service;
import at.uastw.energy.common.config.RabbitConfig;
import at.uastw.energy.common.dto.UserMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService {
    private final RabbitTemplate rabbit;
    public UserService(RabbitTemplate rabbit) { this.rabbit = rabbit; }

    @PostConstruct
    public void startSending() {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                sendUsage();
                try { Thread.sleep(ThreadLocalRandom.current().nextLong(1000, 5001)); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        });
    }

    public void sendUsage() {
        LocalTime now = LocalTime.now();
        double factor = (now.isBefore(LocalTime.of(9,0)) || now.isAfter(LocalTime.of(17,0))) ? 0.5 : 1.5;
        double base = ThreadLocalRandom.current().nextDouble(0.001, 0.005);
        double kwh = base * factor;
        UserMessage msg = new UserMessage("USER","COMMUNITY",kwh,LocalDateTime.now());
        rabbit.convertAndSend(RabbitConfig.EXCHANGE, "energy.user", msg);
    }
}