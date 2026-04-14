package com.microbench.server;

import com.microbench.ast.ASTNode;
import com.microbench.ast.Program;
import com.microbench.backend.CGenerator;
import com.microbench.backend.JavaGenerator;
import com.microbench.engine.BenchmarkResult;
import com.microbench.engine.ExecutionEngine;
import com.microbench.engine.ExecutionResult;
import com.microbench.frontend.ASTBuilder;
import com.microbench.frontend.parser.MicroLexer;
import com.microbench.frontend.parser.MicroParser;
import com.microbench.persistence.BenchmarkRunDao;
import com.microbench.persistence.BenchmarkRunEntity;
import com.microbench.persistence.DatabaseManager;
import com.microbench.persistence.ProgramDao;
import com.microbench.persistence.ProgramEntity;
import com.microbench.rmi.RemoteCompilerService;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class CompilerServer extends UnicastRemoteObject implements RemoteCompilerService {

    private final DatabaseManager dbManager;
    private final ProgramDao programDao;
    private final BenchmarkRunDao benchmarkRunDao;
    private final ExecutionEngine engine;

    protected CompilerServer() throws RemoteException {
        super();
        try {
            dbManager = new DatabaseManager();
            dbManager.initializeSchema();
            programDao = new ProgramDao(dbManager);
            benchmarkRunDao = new BenchmarkRunDao(dbManager);
            engine = new ExecutionEngine();
        } catch (Exception e) {
            throw new RemoteException("Failed to initialize server resources", e);
        }
    }

    @Override
    public BenchmarkResult compileAndRun(String sourceCode, String programName, String optLevel, boolean warmup, int warmupIters) throws RemoteException {
        try {
            System.out.println("Received compileAndRun request for: " + programName);

            MicroLexer lexer = new MicroLexer(CharStreams.fromString(sourceCode));
            MicroParser parser = new MicroParser(new CommonTokenStream(lexer));
            ASTBuilder astBuilder = new ASTBuilder();
            ASTNode ast = astBuilder.visit(parser.program());

            if (!(ast instanceof Program)) {
                throw new Exception("Failed to parse into a valid Program.");
            }
            Program program = (Program) ast;

            JavaGenerator javaGen = new JavaGenerator();
            String javaCode = javaGen.visit(program);

            CGenerator cGen = new CGenerator();
            String cCode = cGen.visit(program);

            BenchmarkResult result = engine.runBenchmark(javaCode, cCode, warmup, warmupIters, optLevel);

            // Persist
            ExecutionResult jvm = result.getJavaResult();
            ExecutionResult nat = result.getCResult();

            ProgramEntity progEntity = programDao.saveOrGet(programName, sourceCode, "");
            if (progEntity != null) {
                benchmarkRunDao.save(progEntity.getId(), jvm.getExecutionTimeNs(), nat.getExecutionTimeNs(),
                        jvm.getPeakMemoryKb(), nat.getPeakMemoryKb(), jvm.getExitCode(), nat.getExitCode(),
                        optLevel, warmup, warmupIters);
            }

            System.out.println("Finished compileAndRun request for: " + programName);
            return result;
        } catch (Exception e) {
            System.err.println("Error in compileAndRun: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Execution failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BenchmarkRunEntity> getRecentRuns() throws RemoteException {
        try {
            System.out.println("Received getRecentRuns request");
            return benchmarkRunDao.listRecent(20);
        } catch (Exception e) {
            System.err.println("Error in getRecentRuns: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Failed to list recent runs", e);
        }
    }

    @Override
    public String ping() throws RemoteException {
        return "PONG";
    }

    public static void main(String[] args) {
        try {
            CompilerServer server = new CompilerServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("CompilerService", server);
            System.out.println("RMI Compiler Server ready on port 1099");
        } catch (Exception e) {
            System.err.println("CompilerServer exception:");
            e.printStackTrace();
        }
    }
}
