package at.uastw.energy.usage.service;
import at.uastw.energy.common.config.RabbitConfig;
import at.uastw.energy.common.dto.ProducerMessage;
import at.uastw.energy.common.dto.UserMessage;
import at.uastw.energy.common.dto.UsageUpdateMessage;
import at.uastw.energy.usage.entity.UsageRecord;
import at.uastw.energy.usage.repository.UsageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
@Service
public class UsageListener {
    private final UsageRepository repo;
    private final RabbitTemplate rabbit;
    public UsageListener(UsageRepository repo,RabbitTemplate rabbit){this.repo=repo;this.rabbit=rabbit;}
    @RabbitListener(queues=RabbitConfig.QUEUE_PRODUCER)
    public void onProducer(ProducerMessage msg){aggregate(msg.datetime().withMinute(0).withSecond(0).withNano(0),msg.kwh(),0);}
    @RabbitListener(queues=RabbitConfig.QUEUE_USER)
    public void onUser(UserMessage msg){aggregate(msg.datetime().withMinute(0).withSecond(0).withNano(0),0,msg.kwh());}
    private synchronized void aggregate(LocalDateTime hour,double prod,double use){
        UsageRecord ur=repo.findById(hour).orElse(new UsageRecord(hour,0,0,0));
        double newProd=ur.getCommunityProduced()+prod;
        double newUse=ur.getCommunityUsed()+use;
        double newGrid=Math.max(0,newUse-newProd);
        repo.save(new UsageRecord(hour,newProd,newUse,newGrid));
        rabbit.convertAndSend(RabbitConfig.EXCHANGE,"energy.usage",
            new UsageUpdateMessage(hour,newProd,newUse,newGrid));
    }
}