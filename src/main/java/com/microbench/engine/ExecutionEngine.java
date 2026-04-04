package com.microbench.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExecutionEngine {

    public ExecutionResult runJava(String javaCode) throws IOException, InterruptedException {
        // Create a temporary directory for our compilation
        Path tempDir = Files.createTempDirectory("microbench_java");
        File sourceFile = new File(tempDir.toFile(), "Main.java");
        Files.write(sourceFile.toPath(), javaCode.getBytes());

        // Compile
        ProcessBuilder compilePb = new ProcessBuilder("javac", sourceFile.getAbsolutePath());
        compilePb.directory(tempDir.toFile());
        Process compileProc = compilePb.start();
        int compileExitCode = compileProc.waitFor();

        if (compileExitCode != 0) {
            String errorOutput = readStream(compileProc.getErrorStream());
            return new ExecutionResult(false, "Compilation Failed: " + errorOutput, -1, -1);
        }

        // Run
        ProcessBuilder runPb = new ProcessBuilder("/usr/bin/time", "-v", "java", "Main");
        runPb.directory(tempDir.toFile());
        return executeAndParseMetrics(runPb);
    }

    public ExecutionResult runC(String cCode) throws IOException, InterruptedException {
        // Create a temporary directory
        Path tempDir = Files.createTempDirectory("microbench_c");
        File sourceFile = new File(tempDir.toFile(), "main.c");
        Files.write(sourceFile.toPath(), cCode.getBytes());

        // Compile
        ProcessBuilder compilePb = new ProcessBuilder("gcc", "-O3", "-o", "main_exec", sourceFile.getAbsolutePath());
        compilePb.directory(tempDir.toFile());
        Process compileProc = compilePb.start();
        int compileExitCode = compileProc.waitFor();

        if (compileExitCode != 0) {
            String errorOutput = readStream(compileProc.getErrorStream());
            return new ExecutionResult(false, "Compilation Failed: " + errorOutput, -1, -1);
        }

        // Run
        ProcessBuilder runPb = new ProcessBuilder("/usr/bin/time", "-v", "./main_exec");
        runPb.directory(tempDir.toFile());
        return executeAndParseMetrics(runPb);
    }

    private ExecutionResult executeAndParseMetrics(ProcessBuilder pb) throws IOException, InterruptedException {
        // time -v writes to stderr, program writes to stdout
        pb.redirectErrorStream(false);
        Process process = pb.start();

        String standardOutput = readStream(process.getInputStream());
        String timeOutput = readStream(process.getErrorStream());

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            return new ExecutionResult(false, "Execution failed with code " + exitCode + ": " + timeOutput, -1, -1);
        }

        long executionTimeNs = -1;
        long peakMemoryKb = -1;

        // Parse program output for our injected timing metric
        for (String line : standardOutput.split("\\r?\\n")) {
            if (line.startsWith("__METRIC_TIME_NS:")) {
                try {
                    executionTimeNs = Long.parseLong(line.substring("__METRIC_TIME_NS:".length()).trim());
                } catch (NumberFormatException ignored) {}
            }
        }

        // Clean up program output (remove the metric line)
        String cleanOutput = standardOutput.replaceAll("__METRIC_TIME_NS:.*\\r?\\n?", "");

        // Parse `/usr/bin/time -v` output for peak memory
        // Look for: "Maximum resident set size (kbytes): 12345"
        for (String line : timeOutput.split("\\r?\\n")) {
            if (line.contains("Maximum resident set size (kbytes):")) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    try {
                        peakMemoryKb = Long.parseLong(parts[1].trim());
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        return new ExecutionResult(true, cleanOutput, executionTimeNs, peakMemoryKb);
    }

    private String readStream(java.io.InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
