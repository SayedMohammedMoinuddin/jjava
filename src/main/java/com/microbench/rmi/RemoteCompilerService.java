package com.microbench.rmi;

import com.microbench.engine.BenchmarkResult;
import com.microbench.persistence.BenchmarkRunEntity;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteCompilerService extends Remote {

    /**
     * Compiles and runs the given source code with the specified options.
     * Returns a BenchmarkResult which contains execution details for both JVM and Native.
     */
    BenchmarkResult compileAndRun(String sourceCode, String programName, String optLevel, boolean warmup, int warmupIters) throws RemoteException;

    /**
     * Retrieves the recent benchmark runs from the server database.
     */
    List<BenchmarkRunEntity> getRecentRuns() throws RemoteException;

    /**
     * Used by client to verify server connection.
     */
    String ping() throws RemoteException;
}
