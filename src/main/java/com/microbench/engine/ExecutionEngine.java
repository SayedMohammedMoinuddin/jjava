package com.microbench.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExecutionEngine {

    public BenchmarkResult runBenchmark(String javaCode, String cCode, boolean enableWarmup, int warmupIterations, String optimizationLevel) throws IOException, InterruptedException {
        int iters = enableWarmup ? warmupIterations : 0;

        ExecutionResult javaResult = runJava(javaCode, iters);
        ExecutionResult cResult = runC(cCode, iters, optimizationLevel);

        return new BenchmarkResult(javaResult, cResult);
    }

    private ExecutionResult runJava(String javaCode, int warmupIterations) throws IOException, InterruptedException {
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
            return new ExecutionResult(false, "Compilation Failed: " + errorOutput, -1, -1, compileExitCode, errorOutput);
        }

        // Run
        ProcessBuilder runPb = new ProcessBuilder("/usr/bin/time", "-v", "java", "Main", String.valueOf(warmupIterations));
        runPb.directory(tempDir.toFile());
        return executeAndParseMetrics(runPb);
    }

    private ExecutionResult runC(String cCode, int warmupIterations, String optimizationLevel) throws IOException, InterruptedException {
        // Create a temporary directory
        Path tempDir = Files.createTempDirectory("microbench_c");
        File sourceFile = new File(tempDir.toFile(), "main.c");
        Files.write(sourceFile.toPath(), cCode.getBytes());

        // Compile
        ProcessBuilder compilePb = new ProcessBuilder("gcc", optimizationLevel, "-o", "main_exec", sourceFile.getAbsolutePath());
        compilePb.directory(tempDir.toFile());
        Process compileProc = compilePb.start();
        int compileExitCode = compileProc.waitFor();

        if (compileExitCode != 0) {
            String errorOutput = readStream(compileProc.getErrorStream());
            return new ExecutionResult(false, "Compilation Failed: " + errorOutput, -1, -1, compileExitCode, errorOutput);
        }

        // Run
        ProcessBuilder runPb = new ProcessBuilder("/usr/bin/time", "-v", "./main_exec", String.valueOf(warmupIterations));
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

        long executionTimeNs = -1;
        long peakMemoryKb = -1;

        // Parse program output for our injected timing metric
        for (String line : standardOutput.split("\\r?\\n")) {
            if (line.startsWith("BENCH_TIME_NS=")) {
                try {
                    executionTimeNs = Long.parseLong(line.substring("BENCH_TIME_NS=".length()).trim());
                } catch (NumberFormatException ignored) {}
            }
        }

        // Clean up program output (remove the metric line and warmup messages)
        String cleanOutput = standardOutput.replaceAll("BENCH_TIME_NS=.*\\r?\\n?", "");
        cleanOutput = cleanOutput.replaceAll("__WARMUP_DONE\\r?\\n?", "");

        // We also want to only capture the final run's output to not spam the UI with 1000s of identical output lines from warmup runs.
        // We can do this by splitting at __WARMUP_DONE.
        if (standardOutput.contains("__WARMUP_DONE")) {
             String[] split = standardOutput.split("__WARMUP_DONE\\r?\\n?");
             if (split.length > 1) {
                 cleanOutput = split[split.length - 1].replaceAll("BENCH_TIME_NS=.*\\r?\\n?", "");
             }
        }


        // Parse `/usr/bin/time -v` output for peak memory using Regex for robustness
        Pattern rssPattern = Pattern.compile("Maximum resident set size \\(kbytes\\):\\s*(\\d+)");
        Matcher matcher = rssPattern.matcher(timeOutput);
        if (matcher.find()) {
            try {
                peakMemoryKb = Long.parseLong(matcher.group(1));
            } catch (NumberFormatException ignored) {}
        }

        String rawLogs = "Standard Output:\n" + standardOutput + "\nStandard Error/Time output:\n" + timeOutput;

        boolean success = exitCode == 0;
        String finalOutput = success ? cleanOutput : "Execution failed with code " + exitCode + ":\n" + timeOutput;

        return new ExecutionResult(success, finalOutput, executionTimeNs, peakMemoryKb, exitCode, rawLogs);
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
