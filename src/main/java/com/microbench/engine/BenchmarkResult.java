package com.microbench.engine;

public class BenchmarkResult {
    private final ExecutionResult javaResult;
    private final ExecutionResult cResult;

    public BenchmarkResult(ExecutionResult javaResult, ExecutionResult cResult) {
        this.javaResult = javaResult;
        this.cResult = cResult;
    }

    public ExecutionResult getJavaResult() {
        return javaResult;
    }

    public ExecutionResult getCResult() {
        return cResult;
    }
}
