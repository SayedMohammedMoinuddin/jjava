package com.microbench;

import com.microbench.ast.ASTNode;
import com.microbench.ast.Program;
import com.microbench.backend.CGenerator;
import com.microbench.backend.JavaGenerator;
import com.microbench.engine.ExecutionEngine;
import com.microbench.engine.ExecutionResult;
import com.microbench.engine.BenchmarkResult;
import com.microbench.frontend.ASTBuilder;
import com.microbench.frontend.parser.MicroLexer;
import com.microbench.frontend.parser.MicroParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.IOException;

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
            System.out.println("Processing file: " + filePath);

            // 1. Parsing and AST Generation
            CharStream input = CharStreams.fromFileName(filePath);
            MicroLexer lexer = new MicroLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MicroParser parser = new MicroParser(tokens);

            MicroParser.ProgramContext tree = parser.program();
            ASTBuilder astBuilder = new ASTBuilder();
            ASTNode ast = astBuilder.visit(tree);

            if (!(ast instanceof Program)) {
                System.err.println("Error: Could not generate valid AST.");
                System.exit(1);
            }

            Program program = (Program) ast;

            // 2. Code Generation
            JavaGenerator javaGenerator = new JavaGenerator();
            String javaCode = javaGenerator.visit(program);

            CGenerator cGenerator = new CGenerator();
            String cCode = cGenerator.visit(program);

            System.out.println("\n--- Generated Java Code ---\n" + javaCode);
            System.out.println("--- Generated C Code ---\n" + cCode);

            // 3. Execution and Telemetry
            ExecutionEngine engine = new ExecutionEngine();

            boolean enableWarmup = true;
            int warmupIterations = 1000;
            String optimizationLevel = "-O3";

            System.out.println("--- Running Benchmarks (Warmup: " + enableWarmup + ", Iters: " + warmupIterations + ", Opt: " + optimizationLevel + ") ---");

            BenchmarkResult benchmarkResult = engine.runBenchmark(javaCode, cCode, enableWarmup, warmupIterations, optimizationLevel);

            System.out.println("\n--- Java Results ---");
            printResult("Java", benchmarkResult.getJavaResult());

            System.out.println("\n--- Native (C) Results ---");
            printResult("Native (C)", benchmarkResult.getCResult());

        } catch (IOException | InterruptedException e) {
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
