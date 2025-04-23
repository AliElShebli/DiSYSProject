package at.uastw.energy;

public class CurrentPercentage {
    public String hour;
    public double communityDepleted;
    public double gridPortion;

    public CurrentPercentage(String hour, double depleted, double portion) {
        this.hour = hour;
        this.communityDepleted = depleted;
        this.gridPortion = portion;
    }
}