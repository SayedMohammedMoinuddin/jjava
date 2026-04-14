package com.microbench.engine;

import java.io.Serializable;

public class BenchmarkResult implements Serializable {
    private static final long serialVersionUID = 1L;

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
