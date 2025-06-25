package at.uastw.energy.percentage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"at.uastw.energy.common.config","at.uastw.energy.percentage"})
public class PercentageServiceApplication {
  public static void main(String[] args){SpringApplication.run(PercentageServiceApplication.class,args);}
}