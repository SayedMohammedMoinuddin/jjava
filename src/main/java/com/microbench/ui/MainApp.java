package com.microbench.ui;

import com.microbench.engine.BenchmarkResult;
import com.microbench.engine.ExecutionResult;
import com.microbench.persistence.BenchmarkRunEntity;
import com.microbench.rmi.RemoteCompilerClient;
import com.microbench.rmi.RemoteCompilerService;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApp extends Application {

    private RemoteCompilerClient client;
    private ExecutorService executorService;

    // UI Components
    private TextField programNameField;
    private ComboBox<String> optLevelBox;
    private CheckBox warmupCheck;
    private TextField warmupItersField;
    private Button runButton;
    private TextArea editorArea;
    private TextArea consoleArea;
    private BarChart<String, Number> timeChart;
    private BarChart<String, Number> memoryChart;
    private ListView<String> historyList;
    private List<BenchmarkRunEntity> recentRuns;

    @Override
    public void init() throws Exception {
        client = new RemoteCompilerClient("localhost", 1099);
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void stop() throws Exception {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Dual-Target Micro-Compiler & Execution Benchmarker");

        // Top Control Bar
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));

        programNameField = new TextField("MyProgram");
        programNameField.setPromptText("Program Name");

        optLevelBox = new ComboBox<>(FXCollections.observableArrayList("-O0", "-O3"));
        optLevelBox.setValue("-O3");

        warmupCheck = new CheckBox("Warmup");
        warmupCheck.setSelected(true);

        warmupItersField = new TextField("1000");
        warmupItersField.setPrefWidth(60);

        runButton = new Button("Run Benchmark");
        runButton.setOnAction(e -> runBenchmark());

        topBar.getChildren().addAll(
                new Label("Name:"), programNameField,
                new Label("C Opt:"), optLevelBox,
                warmupCheck, warmupItersField,
                runButton
        );

        // Center Split (Editor + Console)
        SplitPane centerSplit = new SplitPane();

        editorArea = new TextArea();
        editorArea.setText("""
int max = 10000;
int sum = 0;
for (int i = 0; i < max; i = i + 1) {
    sum = sum + i;
}
print(sum);
        """);
        editorArea.setStyle("-fx-font-family: 'monospace';");

        consoleArea = new TextArea();
        consoleArea.setEditable(false);
        consoleArea.setStyle("-fx-font-family: 'monospace';");

        centerSplit.getItems().addAll(editorArea, consoleArea);
        centerSplit.setDividerPositions(0.5);

        // Bottom Charts
        HBox bottomBox = new HBox(10);
        bottomBox.setPadding(new Insets(10));

        CategoryAxis xAxisTime = new CategoryAxis();
        NumberAxis yAxisTime = new NumberAxis();
        yAxisTime.setLabel("Time (ms)");
        timeChart = new BarChart<>(xAxisTime, yAxisTime);
        timeChart.setTitle("Execution Time");
        timeChart.setAnimated(false);

        CategoryAxis xAxisMem = new CategoryAxis();
        NumberAxis yAxisMem = new NumberAxis();
        yAxisMem.setLabel("Peak Memory (KB)");
        memoryChart = new BarChart<>(xAxisMem, yAxisMem);
        memoryChart.setTitle("Peak Memory (RSS)");
        memoryChart.setAnimated(false);

        HBox.setHgrow(timeChart, Priority.ALWAYS);
        HBox.setHgrow(memoryChart, Priority.ALWAYS);
        bottomBox.getChildren().addAll(timeChart, memoryChart);

        // Right side - History
        VBox rightBox = new VBox(5);
        rightBox.setPadding(new Insets(10));
        rightBox.getChildren().add(new Label("Recent Runs"));
        historyList = new ListView<>();
        historyList.setPrefWidth(200);
        refreshHistory();
        VBox.setVgrow(historyList, Priority.ALWAYS);
        rightBox.getChildren().add(historyList);

        // Main Layout
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(centerSplit);
        root.setBottom(bottomBox);
        root.setRight(rightBox);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void runBenchmark() {
        runButton.setDisable(true);
        consoleArea.clear();
        String source = editorArea.getText();
        String programName = programNameField.getText();
        String optLevel = optLevelBox.getValue();
        boolean warmup = warmupCheck.isSelected();
        int warmupIters = 0;
        try {
            warmupIters = Integer.parseInt(warmupItersField.getText());
        } catch (NumberFormatException ignored) {}

        int finalWarmupIters = warmupIters;

        log("Connecting to remote compiler...");

        Task<BenchmarkResult> task = new Task<BenchmarkResult>() {
            @Override
            protected BenchmarkResult call() throws Exception {
                RemoteCompilerService service = client.getService();
                updateMessage("Compiling and executing remotely...");
                return service.compileAndRun(source, programName, optLevel, warmup, finalWarmupIters);
            }
        };

        task.setOnSucceeded(e -> {
            BenchmarkResult result = task.getValue();
            updateUIWithResult(result);
            refreshHistory();
            runButton.setDisable(false);
        });

        task.setOnFailed(e -> {
            logError("Remote execution failed: " + task.getException().getMessage());
            task.getException().printStackTrace();
            runButton.setDisable(false);
        });

        executorService.submit(task);
    }

    private void updateUIWithResult(BenchmarkResult result) {
        ExecutionResult jvm = result.getJavaResult();
        ExecutionResult nat = result.getCResult();

        log("--- Java Output ---");
        log(jvm.getRawLogs());
        log("\n--- Native Output ---");
        log(nat.getRawLogs());

        // Update Charts
        timeChart.getData().clear();
        XYChart.Series<String, Number> timeSeries = new XYChart.Series<>();
        timeSeries.setName("Duration");
        timeSeries.getData().add(new XYChart.Data<>("JVM", jvm.getExecutionTimeNs() / 1_000_000.0));
        timeSeries.getData().add(new XYChart.Data<>("Native", nat.getExecutionTimeNs() / 1_000_000.0));
        timeChart.getData().add(timeSeries);

        memoryChart.getData().clear();
        XYChart.Series<String, Number> memSeries = new XYChart.Series<>();
        memSeries.setName("Peak RSS");
        memSeries.getData().add(new XYChart.Data<>("JVM", jvm.getPeakMemoryKb()));
        memSeries.getData().add(new XYChart.Data<>("Native", nat.getPeakMemoryKb()));
        memoryChart.getData().add(memSeries);
    }

    private void refreshHistory() {
        Task<List<BenchmarkRunEntity>> task = new Task<List<BenchmarkRunEntity>>() {
            @Override
            protected List<BenchmarkRunEntity> call() throws Exception {
                RemoteCompilerService service = client.getService();
                return service.getRecentRuns(20);
            }
        };

        task.setOnSucceeded(e -> {
            recentRuns = task.getValue();
            historyList.getItems().clear();
            for (BenchmarkRunEntity run : recentRuns) {
                historyList.getItems().add(run.getRunAt() + " [ProgID: " + run.getProgramId() + "]");
            }
        });

        task.setOnFailed(e -> {
            System.err.println("Failed to load history from server: " + task.getException().getMessage());
        });

        executorService.submit(task);
    }

    private void log(String msg) {
        Platform.runLater(() -> consoleArea.appendText(msg + "\n"));
    }

    private void logError(String msg) {
        Platform.runLater(() -> consoleArea.appendText("ERROR: " + msg + "\n"));
    }
}
