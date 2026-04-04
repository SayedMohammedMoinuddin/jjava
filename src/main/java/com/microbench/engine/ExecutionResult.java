package com.microbench.engine;

public class ExecutionResult {
    private final boolean success;
    private final String output;
    private final long executionTimeNs;
    private final long peakMemoryKb;

    public ExecutionResult(boolean success, String output, long executionTimeNs, long peakMemoryKb) {
        this.success = success;
        this.output = output;
        this.executionTimeNs = executionTimeNs;
        this.peakMemoryKb = peakMemoryKb;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getOutput() {
        return output;
    }

    public long getExecutionTimeNs() {
        return executionTimeNs;
    }

    public long getPeakMemoryKb() {
        return peakMemoryKb;
    }

    @Override
    public String toString() {
        return "ExecutionResult{" +
                "success=" + success +
                ", executionTimeNs=" + executionTimeNs +
                ", peakMemoryKb=" + peakMemoryKb +
                ", output='" + output + '\'' +
                '}';
    }
}
