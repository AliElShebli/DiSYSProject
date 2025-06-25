package at.uastw.energy.percentage.repository;

import at.uastw.energy.percentage.entity.PercentageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PercentageRepository
        extends JpaRepository<PercentageRecord, LocalDateTime> {

    /**
     * Liefert den neuesten PercentageRecord (nach hour absteigend).
     */
    Optional<PercentageRecord> findFirstByOrderByHourDesc();
}
