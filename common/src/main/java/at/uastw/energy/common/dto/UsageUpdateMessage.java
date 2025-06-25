package at.uastw.energy.common.dto;
import java.time.LocalDateTime;
public record UsageUpdateMessage(LocalDateTime hour,double communityProduced,double communityUsed,double gridUsed){}