package at.uastw.energy.common.dto;
import java.time.LocalDateTime;
public record PercentageUpdateMessage(LocalDateTime hour,double communityDepleted,double gridPortion){}