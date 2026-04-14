package com.microbench.persistence;
import java.io.Serializable;

public class BenchmarkRunEntity implements Serializable {
    private final long id;
    private final long programId;
    private final long jvmTimeNs;
    private final long nativeTimeNs;
    private final long jvmPeakRssKb;
    private final long nativePeakRssKb;
    private final int jvmExitCode;
    private final int nativeExitCode;
    private final String optimizationLevel;
    private final boolean warmupEnabled;
    private final int warmupIterations;
    private final String runAt; // Used for display

    public BenchmarkRunEntity(long id, long programId, long jvmTimeNs, long nativeTimeNs,
                              long jvmPeakRssKb, long nativePeakRssKb, int jvmExitCode,
                              int nativeExitCode, String optimizationLevel,
                              boolean warmupEnabled, int warmupIterations, String runAt) {
        this.id = id;
        this.programId = programId;
        this.jvmTimeNs = jvmTimeNs;
        this.nativeTimeNs = nativeTimeNs;
        this.jvmPeakRssKb = jvmPeakRssKb;
        this.nativePeakRssKb = nativePeakRssKb;
        this.jvmExitCode = jvmExitCode;
        this.nativeExitCode = nativeExitCode;
        this.optimizationLevel = optimizationLevel;
        this.warmupEnabled = warmupEnabled;
        this.warmupIterations = warmupIterations;
        this.runAt = runAt;
    }

    // Getters for all fields
    public long getId() { return id; }
    public long getProgramId() { return programId; }
    public long getJvmTimeNs() { return jvmTimeNs; }
    public long getNativeTimeNs() { return nativeTimeNs; }
    public long getJvmPeakRssKb() { return jvmPeakRssKb; }
    public long getNativePeakRssKb() { return nativePeakRssKb; }
    public int getJvmExitCode() { return jvmExitCode; }
    public int getNativeExitCode() { return nativeExitCode; }
    public String getOptimizationLevel() { return optimizationLevel; }
    public boolean isWarmupEnabled() { return warmupEnabled; }
    public int getWarmupIterations() { return warmupIterations; }
    public String getRunAt() { return runAt; }
}
