package com.microbench.backend;

import com.microbench.ast.*;

import java.util.HashMap;
import java.util.Map;

public class CGenerator implements ASTVisitor<String> {

    // A simple hack to keep track of variable types for correct printf formatting
    private final Map<String, String> varTypes = new HashMap<>();

    @Override
    public String visit(Program program) {
        varTypes.clear();

        StringBuilder sb = new StringBuilder();
        sb.append("#include <stdio.h>\n");
        sb.append("#include <stdlib.h>\n");
        sb.append("#include <time.h>\n\n");
        sb.append("int main(int argc, char** argv) {\n");
        sb.append("    int warmup_iters = atoi(argv[1]);\n");
        sb.append("    for (int w = 0; w <= warmup_iters; w++) {\n");
        sb.append("        if (w == warmup_iters) {\n");
        sb.append("            printf(\"__WARMUP_DONE\\n\");\n");
        sb.append("        }\n");

        sb.append("        struct timespec start, end;\n");
        sb.append("        clock_gettime(CLOCK_MONOTONIC, &start);\n\n");

        for (Statement stmt : program.getStatements()) {
            sb.append("        ").append(stmt.accept(this).replace("\n", "\n        ")).append("\n");
        }

        sb.append("\n        clock_gettime(CLOCK_MONOTONIC, &end);\n");
        sb.append("        if (w == warmup_iters) {\n");
        sb.append("            long long elapsed_ns = (end.tv_sec - start.tv_sec) * 1000000000LL + (end.tv_nsec - start.tv_nsec);\n");
        sb.append("            printf(\"BENCH_TIME_NS=%lld\\n\", elapsed_ns);\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("    return 0;\n");
        sb.append("}\n");

        return sb.toString();
    }

    @Override
    public String visit(VarDecl varDecl) {
        varTypes.put(varDecl.getName(), varDecl.getType());
        return varDecl.getType() + " " + varDecl.getName() + " = " + varDecl.getExpr().accept(this) + ";";
    }

    @Override
    public String visit(AssignStmt assignStmt) {
        return assignStmt.getName() + " = " + assignStmt.getExpr().accept(this) + ";";
    }

    @Override
    public String visit(PrintStmt printStmt) {
        Expression expr = printStmt.getExpr();

        // Simple type inference for print formatting
        String formatSpecifier = "%d"; // default to int

        if (expr instanceof IdentifierExpr) {
            String varName = ((IdentifierExpr) expr).getName();
            if ("double".equals(varTypes.get(varName))) {
                formatSpecifier = "%f";
            }
        } else if (expr instanceof LiteralExpr) {
            if ("double".equals(((LiteralExpr) expr).getType())) {
                formatSpecifier = "%f";
            }
        }

        return "printf(\"" + formatSpecifier + "\\n\", " + expr.accept(this) + ");";
    }

    @Override
    public String visit(ForLoop forLoop) {
        StringBuilder sb = new StringBuilder();
        sb.append("for (");
        sb.append(forLoop.getInit().accept(this)).append(" ");
        sb.append(forLoop.getCondition().accept(this)).append("; ");
        sb.append(forLoop.getUpdateVar()).append(" = ").append(forLoop.getUpdateExpr().accept(this)).append(") {\n");

        for (Statement stmt : forLoop.getBody()) {
            sb.append("        ").append(stmt.accept(this)).append("\n");
        }
        sb.append("    }");
        return sb.toString();
    }

    @Override
    public String visit(WhileLoop whileLoop) {
        StringBuilder sb = new StringBuilder();
        sb.append("while (").append(whileLoop.getCondition().accept(this)).append(") {\n");
        for (Statement stmt : whileLoop.getBody()) {
            sb.append("        ").append(stmt.accept(this)).append("\n");
        }
        sb.append("    }");
        return sb.toString();
    }

    @Override
    public String visit(BinaryExpr binaryExpr) {
        return "(" + binaryExpr.getLeft().accept(this) + " " + binaryExpr.getOperator() + " " + binaryExpr.getRight().accept(this) + ")";
    }

    @Override
    public String visit(LiteralExpr literalExpr) {
        return literalExpr.getValue();
    }

    @Override
    public String visit(IdentifierExpr identifierExpr) {
        return identifierExpr.getName();
    }
}
