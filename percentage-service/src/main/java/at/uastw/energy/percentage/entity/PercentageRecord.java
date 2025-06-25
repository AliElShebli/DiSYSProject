package at.uastw.energy.percentage.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name="percentage")
public class PercentageRecord {
  @Id private LocalDateTime hour;
  private double communityDepleted;
  private double gridPortion;
  public PercentageRecord() {}
  public PercentageRecord(LocalDateTime hour,double cd,double gp){this.hour=hour;this.communityDepleted=cd;this.gridPortion=gp;}
  public LocalDateTime getHour(){return hour;}
  public double getCommunityDepleted(){return communityDepleted;}
  public double getGridPortion(){return gridPortion;}
}