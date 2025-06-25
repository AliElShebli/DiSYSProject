package at.uastw.energy.gui;

// Jetzt „used“ statt „depleted“
public record GuiPercentage(
        double communityUsedPercent,
        double gridPortionPercent
) {}
