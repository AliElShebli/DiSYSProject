package at.uastw.energy.producer.service;

import at.uastw.energy.common.config.RabbitConfig;
import at.uastw.energy.common.dto.ProducerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ProducerService {

  private static final Logger log = LoggerFactory.getLogger(ProducerService.class);

  private final RabbitTemplate rabbit;
  private final WebClient webClient;
  private final String apiKey;
  private final double lat, lon;

  public ProducerService(
          RabbitTemplate rabbit,
          @Value("${producer.weather.api-key}") String apiKey,
          @Value("${producer.weather.lat}") double lat,
          @Value("${producer.weather.lon}") double lon,
          @Value("${producer.weather.url}") String baseUrl
  ) {
    this.rabbit = rabbit;
    this.apiKey = apiKey;
    this.lat = lat;
    this.lon = lon;
    this.webClient = WebClient.create(baseUrl);
  }

  @Scheduled(fixedDelayString = "#{T(java.util.concurrent.ThreadLocalRandom).current().nextLong(" +
          "${producer.send-interval.min:1}000," +
          "${producer.send-interval.max:5}000)}")
  public void produce() {
    // 1) Aktuelles Wetter abrufen
    double sunFactor = webClient.get()
            .uri(uri -> uri
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .build())
            .retrieve()
            .bodyToMono(Map.class)
            .map(this::extractCurrentSunlight)
            .blockOptional()
            .orElse(0.5);

    // 2) Zufalls-Basis generieren
    double base = ThreadLocalRandom.current().nextDouble(0.001, 0.005);
    double kwh  = Math.max(0, base * sunFactor);

    // 3) In RabbitMQ schieben
    rabbit.convertAndSend(
            RabbitConfig.EXCHANGE,
            "energy.producer",
            new ProducerMessage("PRODUCER","COMMUNITY", kwh, LocalDateTime.now())
    );

    // 4) Loggen
    log.info("Produced {} kWh (sunFactor: {})",
            String.format("%.4f", kwh),
            String.format("%.2f", sunFactor));
  }

  @SuppressWarnings("unchecked")
  private double extractCurrentSunlight(Map<?,?> json) {
    // Im /weather-Response ist "clouds" ein Objekt mit "all"
    Map<String, Object> clouds = (Map<String, Object>) json.get("clouds");
    // falls null oder kein Eintrag, auf 50% Wolken setzen
    double cloudPercent = (clouds != null)
            ? ((Number) clouds.getOrDefault("all", 50)).doubleValue()
            : 50;
    return (100 - cloudPercent) / 100;
  }
}
