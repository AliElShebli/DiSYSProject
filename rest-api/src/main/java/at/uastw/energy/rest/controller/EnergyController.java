package at.uastw.energy.rest.controller;

import at.uastw.energy.usage.entity.UsageRecord;
import at.uastw.energy.usage.repository.UsageRepository;
import at.uastw.energy.gui.GuiPercentage;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final UsageRepository usageRepo;

    public EnergyController(UsageRepository usageRepo) {
        this.usageRepo = usageRepo;
    }

    /**
     * Liefert die Gesamt-Prozente über alle UsageRecord:
     *   communityUsedPercent = (sumCommunityUsed / sumCommunityProduced) * 100
     *   gridPortionPercent    = (sumGridUsed / (sumCommunityUsed + sumGridUsed)) * 100
     */
    @GetMapping("/current")
    public GuiPercentage getCurrent() {
        List<UsageRecord> all = usageRepo.findAll();

        double sumProduced = all.stream()
                .mapToDouble(UsageRecord::getCommunityProduced)
                .sum();
        double sumUsed = all.stream()
                .mapToDouble(UsageRecord::getCommunityUsed)
                .sum();
        double sumGrid = all.stream()
                .mapToDouble(UsageRecord::getGridUsed)
                .sum();

        double communityUsedPct = sumProduced == 0
                ? 0
                : (sumUsed / sumProduced) * 100;

        double gridPortionPct = (sumUsed + sumGrid) == 0
                ? 0
                : (sumGrid / (sumUsed + sumGrid)) * 100;

        return new GuiPercentage(communityUsedPct, gridPortionPct);
    }

    /**
     * Liefert alle UsageRecord zwischen start und end (inclusive).
     */
    @GetMapping("/historical")
    public List<UsageRecord> getHistorical(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
            java.time.LocalDateTime start,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
            java.time.LocalDateTime end
    ) {
        return usageRepo.findAllByTimestampBetween(start, end);
    }
}
