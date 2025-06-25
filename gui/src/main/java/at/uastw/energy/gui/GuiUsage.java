package at.uastw.energy.gui;

import java.time.LocalDateTime;

public record GuiUsage(
        LocalDateTime timestamp,
        double communityProduced,
        double communityUsed,
        double gridUsed
) {}
