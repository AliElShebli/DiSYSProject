package at.uastw.energy.controller;

import at.uastw.energy.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final List<EnergyStats> energyData = Arrays.asList(
        new EnergyStats("2025-01-10T14:00:00", 143.024, 130.101, 14.75),
        new EnergyStats("2025-01-09T14:00:00", 150.0, 125.0, 10.0)
    );

    private final CurrentPercentage current = new CurrentPercentage("2025-01-10T14:00:00", 78.54, 7.23);

    @GetMapping("/current")
    public CurrentPercentage getCurrent() {
        return current;
    }

    @GetMapping("/historical")
    public List<EnergyStats> getHistorical(@RequestParam String start, @RequestParam String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);

        return energyData.stream()
                .filter(e -> {
                    LocalDateTime dt = LocalDateTime.parse(e.hour);
                    return !dt.isBefore(startTime) && !dt.isAfter(endTime);
                })
                .collect(Collectors.toList());
    }
}