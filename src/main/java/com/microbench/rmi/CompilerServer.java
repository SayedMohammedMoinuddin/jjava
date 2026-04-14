package com.microbench.rmi;

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

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class CompilerServer extends UnicastRemoteObject implements RemoteCompilerService {

    private static final long serialVersionUID = 1L;

    private final ExecutionEngine engine;
    private final DatabaseManager dbManager;
    private final ProgramDao programDao;
    private final BenchmarkRunDao benchmarkRunDao;

    protected CompilerServer() throws RemoteException {
        super();
        this.engine = new ExecutionEngine();
        this.dbManager = new DatabaseManager();
        this.dbManager.initializeSchema();
        this.programDao = new ProgramDao(dbManager);
        this.benchmarkRunDao = new BenchmarkRunDao(dbManager);
    }

    @Override
    public String ping() throws RemoteException {
        return "PONG";
    }

    @Override
    public BenchmarkResult compileAndRun(String sourceCode, String programName, String optLevel, boolean warmupEnabled, int warmupIters) throws RemoteException {
        try {
            System.out.println("Processing job for program: " + programName);

            // 1. Parsing and AST Generation
            MicroLexer lexer = new MicroLexer(CharStreams.fromString(sourceCode));
            MicroParser parser = new MicroParser(new CommonTokenStream(lexer));
            ASTBuilder astBuilder = new ASTBuilder();
            ASTNode ast = astBuilder.visit(parser.program());

            if (!(ast instanceof Program)) {
                ExecutionResult failed = new ExecutionResult(false, "Failed to parse into a valid Program.", -1, -1, -1, "AST Generation Error");
                return new BenchmarkResult(failed, failed);
            }
            Program program = (Program) ast;

            // 2. Code Generation
            JavaGenerator javaGen = new JavaGenerator();
            String javaCode = javaGen.visit(program);

            CGenerator cGen = new CGenerator();
            String cCode = cGen.visit(program);

            // 3. Run Benchmark
            BenchmarkResult result = engine.runBenchmark(javaCode, cCode, warmupEnabled, warmupIters, optLevel);

            // 4. Persistence
            ExecutionResult jvm = result.getJavaResult();
            ExecutionResult nat = result.getCResult();

            ProgramEntity progEntity = programDao.saveOrGet(programName, sourceCode, "");
            if (progEntity != null && jvm.isSuccess() && nat.isSuccess()) { // We might only want to store successful benchmarks, or all.
                benchmarkRunDao.save(progEntity.getId(), jvm.getExecutionTimeNs(), nat.getExecutionTimeNs(),
                        jvm.getPeakMemoryKb(), nat.getPeakMemoryKb(), jvm.getExitCode(), nat.getExitCode(),
                        optLevel, warmupEnabled, warmupIters);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            ExecutionResult errorRes = new ExecutionResult(false, "Server Error: " + e.getMessage(), -1, -1, -1, "Exception trace on server");
            return new BenchmarkResult(errorRes, errorRes);
        }
    }

    @Override
    public List<BenchmarkRunEntity> getRecentRuns(int limit) throws RemoteException {
        return benchmarkRunDao.listRecent(limit);
    }

    public static void main(String[] args) {
        try {
            int port = 1099;
            LocateRegistry.createRegistry(port);

            CompilerServer server = new CompilerServer();
            Registry registry = LocateRegistry.getRegistry(port);
            registry.rebind("CompilerService", server);

            System.out.println("RMI Compiler Server ready on port " + port);
        } catch (Exception e) {
            System.err.println("CompilerServer exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
