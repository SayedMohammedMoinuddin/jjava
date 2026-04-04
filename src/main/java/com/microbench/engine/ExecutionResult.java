package com.microbench.engine;

public class ExecutionResult {
    private final boolean success;
    private final String output;
    private final long executionTimeNs;
    private final long peakMemoryKb;
    private final int exitCode;
    private final String rawLogs;

    public ExecutionResult(boolean success, String output, long executionTimeNs, long peakMemoryKb, int exitCode, String rawLogs) {
        this.success = success;
        this.output = output;
        this.executionTimeNs = executionTimeNs;
        this.peakMemoryKb = peakMemoryKb;
        this.exitCode = exitCode;
        this.rawLogs = rawLogs;
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

    public int getExitCode() {
        return exitCode;
    }

    public String getRawLogs() {
        return rawLogs;
    }

    @Override
    public String toString() {
        return "ExecutionResult{" +
                "success=" + success +
                ", executionTimeNs=" + executionTimeNs +
                ", peakMemoryKb=" + peakMemoryKb +
                ", exitCode=" + exitCode +
                ", output='" + output + '\'' +
                '}';
    }
}
