package com.microbench.rmi;

import com.microbench.engine.BenchmarkResult;
import com.microbench.persistence.BenchmarkRunEntity;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteCompilerService extends Remote {

    /**
     * Pings the server to check connectivity.
     */
    String ping() throws RemoteException;

    /**
     * Compiles and runs the given micro-language source code for both JVM and Native targets.
     * Persists the execution history in the server-side database.
     */
    BenchmarkResult compileAndRun(String sourceCode, String programName, String optLevel, boolean warmupEnabled, int warmupIters) throws RemoteException;

    /**
     * Fetches the most recent benchmark runs from the server's database.
     */
    List<BenchmarkRunEntity> getRecentRuns(int limit) throws RemoteException;
}
