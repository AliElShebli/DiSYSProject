package at.uastw.energy.usage.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage")
public class UsageRecord {

    /** Wird in der DB tatsächlich als Spalte "hour" abgelegt */
    @Id
    @Column(name = "hour", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "community_produced", nullable = false)
    private double communityProduced;

    @Column(name = "community_used", nullable = false)
    private double communityUsed;

    @Column(name = "grid_used", nullable = false)
    private double gridUsed;

    protected UsageRecord() {
        // JPA braucht einen No-Arg-Konstruktor
    }

    public UsageRecord(LocalDateTime timestamp,
                       double communityProduced,
                       double communityUsed,
                       double gridUsed) {
        this.timestamp = timestamp;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
    }

    /** Für Repository: findAllByTimestampBetween(...) */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gesamtproduktion in kWh (Community)
     */
    public double getProducedKwh() {
        return communityProduced;
    }

    /**
     * Gesamtverbrauch in kWh (Community + Grid)
     */
    public double getUsedKwh() {
        return communityUsed + gridUsed;
    }

    // Optional: Direkte Getter, falls du sie anderswo brauchst
    public double getCommunityProduced() {
        return communityProduced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public double getGridUsed() {
        return gridUsed;
    }
}
