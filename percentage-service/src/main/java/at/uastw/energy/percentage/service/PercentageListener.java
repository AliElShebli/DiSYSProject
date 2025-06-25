package at.uastw.energy.percentage.service;

import at.uastw.energy.common.config.RabbitConfig;
import at.uastw.energy.common.dto.UsageUpdateMessage;
import at.uastw.energy.common.dto.PercentageUpdateMessage;
import at.uastw.energy.percentage.entity.PercentageRecord;
import at.uastw.energy.percentage.repository.PercentageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PercentageListener {

    private final PercentageRepository repo;
    private final RabbitTemplate      rabbit;

    public PercentageListener(PercentageRepository repo,
                              RabbitTemplate rabbit) {
        this.repo   = repo;
        this.rabbit = rabbit;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_USAGE)
    public void onUsage(UsageUpdateMessage msg) {
        double produced = msg.communityProduced();
        double used     = msg.communityUsed();
        double grid     = msg.gridUsed();

        // 1) Community-Pool % = (produced – used) / produced * 100
        double communityPoolPct = (produced == 0)
                ? 0
                : ((produced - used) / produced) * 100;

        // 2) Grid-Portion % = grid / (used + grid) * 100
        double totalUsed = used + grid;
        double gridPortionPct = (totalUsed == 0)
                ? 0
                : (grid / totalUsed) * 100;

        // 3) speichern
        PercentageRecord rec = new PercentageRecord(
                msg.hour(), communityPoolPct, gridPortionPct
        );
        repo.save(rec);

        // 4) weiterschicken
        rabbit.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.QUEUE_PERCENT,
                new PercentageUpdateMessage(
                        msg.hour(),
                        communityPoolPct,
                        gridPortionPct
                )
        );
    }
}
