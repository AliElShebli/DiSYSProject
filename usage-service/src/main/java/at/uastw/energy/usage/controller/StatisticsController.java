package at.uastw.energy.usage.controller;

import at.uastw.energy.usage.service.UsageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/stats")
public class StatisticsController {

    private final UsageService usageService;

    public StatisticsController(UsageService usageService) {
        this.usageService = usageService;
    }

    @GetMapping("/community-pool")
    public double communityPool(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return usageService.calculateCommunityPoolPercentage(start, end);
    }

    @GetMapping("/grid-portion")
    public double gridPortion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return usageService.calculateGridPortionPercentage(start, end);
    }
}
