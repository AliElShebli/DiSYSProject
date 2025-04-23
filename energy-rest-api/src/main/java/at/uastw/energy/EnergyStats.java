package at.uastw.energy;

public class EnergyStats {
    public String hour;
    public double communityProduced;
    public double communityUsed;
    public double gridUsed;

    public EnergyStats(String hour, double produced, double used, double grid) {
        this.hour = hour;
        this.communityProduced = produced;
        this.communityUsed = used;
        this.gridUsed = grid;
    }
}