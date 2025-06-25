package at.uastw.energy.producer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication(scanBasePackages={"at.uastw.energy.common.config","at.uastw.energy.producer"})
@EnableScheduling
public class ProducerApplication{ public static void main(String[] args){ SpringApplication.run(ProducerApplication.class,args); }}