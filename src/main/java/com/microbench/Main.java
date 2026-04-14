package com.microbench;

import com.microbench.engine.ExecutionResult;
import com.microbench.engine.BenchmarkResult;
import com.microbench.rmi.RemoteCompilerClient;
import com.microbench.rmi.RemoteCompilerService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

    public static void main(String[] args) {
        String filePath = "examples/test.micro"; // Default fallback
        if (args.length > 0) {
            filePath = args[0];
        }

        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Error: File not found: " + filePath);
            System.exit(1);
        }

        try {
            System.out.println("Connecting to Remote Compiler Server at localhost:1099...");
            RemoteCompilerClient client = new RemoteCompilerClient("localhost", 1099);
            RemoteCompilerService service = client.getService();

            System.out.println("Reading file: " + filePath);
            String sourceCode = Files.readString(file.toPath());

            boolean enableWarmup = true;
            int warmupIterations = 1000;
            String optimizationLevel = "-O3";

            System.out.println("--- Running Remote Benchmark (Warmup: " + enableWarmup + ", Iters: " + warmupIterations + ", Opt: " + optimizationLevel + ") ---");

            BenchmarkResult benchmarkResult = service.compileAndRun(sourceCode, file.getName(), optimizationLevel, enableWarmup, warmupIterations);

            System.out.println("\n--- Java Results ---");
            printResult("Java", benchmarkResult.getJavaResult());

            System.out.println("\n--- Native (C) Results ---");
            printResult("Native (C)", benchmarkResult.getCResult());

        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printResult(String name, ExecutionResult result) {
        if (result.isSuccess()) {
            System.out.println("Output:\n" + result.getOutput().trim());
            System.out.println("Metrics:");
            System.out.printf("  Execution Time: %.3f ms (%d ns)%n", result.getExecutionTimeNs() / 1_000_000.0, result.getExecutionTimeNs());
            System.out.printf("  Peak Memory: %d KB%n", result.getPeakMemoryKb());
        } else {
            System.out.println(name + " execution failed!");
            System.out.println(result.getOutput());
        }
    }
}
