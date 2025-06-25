package at.uastw.energy.common.dto;
import java.time.LocalDateTime;
public record UserMessage(String type,String association,double kwh,LocalDateTime datetime){}