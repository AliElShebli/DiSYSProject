package at.uastw.energy.usage.repository;

import at.uastw.energy.usage.entity.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UsageRepository extends JpaRepository<UsageRecord, LocalDateTime> {

    /**
     * Holt alle UsageRecord zwischen den beiden Zeitpunkten (inclusive)
     * basierend auf dem Feld 'timestamp'.
     */
    List<UsageRecord> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

}
