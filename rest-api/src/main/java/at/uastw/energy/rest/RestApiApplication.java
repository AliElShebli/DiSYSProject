package at.uastw.energy.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = {
                "at.uastw.energy.rest",         // Deine REST-Controller
                "at.uastw.energy.common.config" // RabbitConfig, Jackson, etc.
        }
)
@EnableJpaRepositories(basePackages = {
        "at.uastw.energy.usage.repository",
        "at.uastw.energy.percentage.repository"
})
@EntityScan(basePackages = {
        "at.uastw.energy.usage.entity",
        "at.uastw.energy.percentage.entity"
})
public class RestApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
    }
}
