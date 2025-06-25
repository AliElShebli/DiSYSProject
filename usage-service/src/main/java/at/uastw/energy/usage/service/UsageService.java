package at.uastw.energy.usage.service;

import at.uastw.energy.usage.entity.UsageRecord;
import at.uastw.energy.usage.repository.UsageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsageService {

    private final UsageRepository repo;

    public UsageService(UsageRepository repo) {
        this.repo = repo;
    }

    /**
     * Community-Pool = (totalProduced – totalUsed) / totalProduced * 100
     */
    public double calculateCommunityPoolPercentage(LocalDateTime start, LocalDateTime end) {
        List<UsageRecord> list = repo.findAllByTimestampBetween(start, end);
        double totalProduced = list.stream()
                .mapToDouble(UsageRecord::getCommunityProduced)
                .sum();
        double totalUsed     = list.stream()
                .mapToDouble(UsageRecord::getCommunityUsed)
                .sum();
        if (totalProduced == 0) {
            return 0.0;
        }
        return (totalProduced - totalUsed) / totalProduced * 100;
    }

    /**
     * Grid-Portion = (totalUsed – totalProduced) / totalUsed * 100
     */
    public double calculateGridPortionPercentage(LocalDateTime start, LocalDateTime end) {
        List<UsageRecord> list = repo.findAllByTimestampBetween(start, end);
        double totalProduced = list.stream()
                .mapToDouble(UsageRecord::getCommunityProduced)
                .sum();
        double totalUsed     = list.stream()
                .mapToDouble(UsageRecord::getCommunityUsed)
                .sum()
                + list.stream()
                .mapToDouble(UsageRecord::getGridUsed)
                .sum(); // falls Du gridUsed dazuzählen willst
        if (totalUsed == 0) {
            return 0.0;
        }
        return (totalUsed - totalProduced) / totalUsed * 100;
    }
}
