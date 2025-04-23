package at.uastw.energy;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.*;
import java.net.http.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EnergyApp extends Application {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Label currentLabel = new Label("Loading current percentage...");
    private final TextField startField = new TextField("2025-01-09T14:00:00");
    private final TextField endField = new TextField("2025-01-10T14:00:00");
    private final TextArea outputArea = new TextArea();

    @Override
    public void start(Stage stage) {
        Button refreshButton = new Button("Refresh Current");
        refreshButton.setOnAction(e -> fetchCurrent());

        Button showDataButton = new Button("Show Data");
        showDataButton.setOnAction(e -> fetchHistorical());

        VBox root = new VBox(10,
            currentLabel,
            refreshButton,
            new Label("Start (ISO datetime):"),
            startField,
            new Label("End (ISO datetime):"),
            endField,
            showDataButton,
            outputArea
        );
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 400, 500);
        stage.setTitle("Energy Community GUI");
        stage.setScene(scene);
        stage.show();

        fetchCurrent();
    }

    private void fetchCurrent() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/energy/current"))
                .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    try {
                        Map<?, ?> data = objectMapper.readValue(json, Map.class);
                        String text = String.format("Community Pool: %.2f%% used\nGrid Portion: %.2f%%",
                                data.get("communityDepleted"), data.get("gridPortion"));
                        currentLabel.setText(text);
                    } catch (Exception ex) {
                        currentLabel.setText("Failed to parse current data.");
                    }
                });
        } catch (Exception e) {
            currentLabel.setText("Error fetching current percentage.");
        }
    }

    private void fetchHistorical() {
        String start = startField.getText();
        String end = endField.getText();
        String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", start, end);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    try {
                        List<Map<String, Object>> list = objectMapper.readValue(json, List.class);
                        StringBuilder sb = new StringBuilder();
                        for (Map<String, Object> row : list) {
                            sb.append(String.format("%s - Produced: %.3f kWh, Used: %.3f kWh, Grid: %.3f kWh\n",
                                    row.get("hour"), row.get("communityProduced"),
                                    row.get("communityUsed"), row.get("gridUsed")));
                        }
                        outputArea.setText(sb.toString());
                    } catch (Exception ex) {
                        outputArea.setText("Failed to parse historical data.");
                    }
                });
        } catch (Exception e) {
            outputArea.setText("Error fetching historical data.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
