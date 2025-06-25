package at.uastw.energy.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

public class GuiApplication extends Application {

    WebClient client = WebClient.create("http://localhost:8080");

    @Override
    public void start(Stage stage) {
        Label lblC = new Label("Community Pool: -- % used");
        Label lblG = new Label("Grid Portion: -- %");
        Button btnR = new Button("Refresh");
        btnR.setOnAction(e -> fetchCurrent(lblC, lblG));

        DatePicker dpS = new DatePicker();
        DatePicker dpE = new DatePicker();
        Button btnS = new Button("Show Data");
        TextArea ta    = new TextArea();
        btnS.setOnAction(e -> {
            LocalDateTime s  = dpS.getValue().atStartOfDay();
            LocalDateTime en = dpE.getValue().atStartOfDay().plusHours(23);
            fetchHistorical(s, en, ta);
        });

        VBox root = new VBox(10,
                lblC, lblG, btnR,
                new Label("Start:"), dpS,
                new Label("End:"),   dpE,
                btnS, ta
        );
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 400, 500));
        stage.setTitle("Energy Community");
        stage.show();
    }

    void fetchCurrent(Label lc, Label lg) {
        client.get().uri("/energy/current")
                .retrieve()
                .bodyToMono(GuiPercentage.class)
                .subscribe(p -> Platform.runLater(() -> {
                    lc.setText(String.format(
                            "Community Pool: %.2f %% used",
                            p.communityUsedPercent()
                    ));
                    lg.setText(String.format(
                            "Grid Portion: %.2f %%",
                            p.gridPortionPercent()
                    ));
                }));
    }

    void fetchHistorical(LocalDateTime s, LocalDateTime e, TextArea ta) {
        client.get().uri(u -> u.path("/energy/historical")
                        .queryParam("start", s)
                        .queryParam("end",   e)
                        .build())
                .retrieve()
                .bodyToFlux(GuiUsage.class)
                .collectList()
                .subscribe(list -> Platform.runLater(() -> {
                    double produced = list.stream()
                            .mapToDouble(GuiUsage::communityProduced).sum();
                    double used     = list.stream()
                            .mapToDouble(GuiUsage::communityUsed).sum();
                    double grid     = list.stream()
                            .mapToDouble(GuiUsage::gridUsed).sum();
                    ta.setText(String.format(
                            "Community produced: %.3f kWh\n" +
                                    "Community used:     %.3f kWh\n" +
                                    "Grid used:          %.3f kWh",
                            produced, used, grid
                    ));
                }));
    }

    public static void main(String[] args) {
        launch();
    }
}
