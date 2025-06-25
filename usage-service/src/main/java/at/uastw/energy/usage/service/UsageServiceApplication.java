package at.uastw.energy.usage.service;

import at.uastw.energy.common.config.RabbitConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "at.uastw.energy.common.config",  // RabbitConfig & related AMQP beans
        "at.uastw.energy.usage"           // Controllers, Services, Listeners
})
@Import(RabbitConfig.class)  // Importiert Deine Rabbit-Konfiguration
@EnableJpaRepositories(basePackages = "at.uastw.energy.usage.repository")
@EntityScan(basePackages = "at.uastw.energy.usage.entity")
public class UsageServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsageServiceApplication.class, args);
    }
}
