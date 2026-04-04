package com.microbench.backend;

import com.microbench.ast.*;

import java.util.List;

public class JavaGenerator implements ASTVisitor<String> {

    @Override
    public String visit(Program program) {
        StringBuilder sb = new StringBuilder();
        sb.append("public class Main {\n");
        sb.append("    public static void main(String[] args) {\n");

        // Setup warmup
        sb.append("        int __warmupIters = Integer.parseInt(args[0]);\n");
        sb.append("        for (int __w = 0; __w <= __warmupIters; __w++) {\n");
        sb.append("            if (__w == __warmupIters) {\n");
        sb.append("                System.out.println(\"__WARMUP_DONE\");\n");
        sb.append("            }\n");

        sb.append("            long __startTime = System.nanoTime();\n\n");

        for (Statement stmt : program.getStatements()) {
            sb.append("            ").append(stmt.accept(this).replace("\n", "\n            ")).append("\n");
        }

        sb.append("\n            long __endTime = System.nanoTime();\n");
        sb.append("            if (__w == __warmupIters) {\n");
        sb.append("                System.out.println(\"BENCH_TIME_NS=\" + (__endTime - __startTime));\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    @Override
    public String visit(VarDecl varDecl) {
        return varDecl.getType() + " " + varDecl.getName() + " = " + varDecl.getExpr().accept(this) + ";";
    }

    @Override
    public String visit(AssignStmt assignStmt) {
        return assignStmt.getName() + " = " + assignStmt.getExpr().accept(this) + ";";
    }

    @Override
    public String visit(PrintStmt printStmt) {
        return "System.out.println(" + printStmt.getExpr().accept(this) + ");";
    }

    @Override
    public String visit(ForLoop forLoop) {
        StringBuilder sb = new StringBuilder();
        sb.append("for (");
        sb.append(forLoop.getInit().accept(this)).append(" ");
        sb.append(forLoop.getCondition().accept(this)).append("; ");
        sb.append(forLoop.getUpdateVar()).append(" = ").append(forLoop.getUpdateExpr().accept(this)).append(") {\n");

        for (Statement stmt : forLoop.getBody()) {
            sb.append("            ").append(stmt.accept(this)).append("\n");
        }
        sb.append("        }");
        return sb.toString();
    }

    @Override
    public String visit(WhileLoop whileLoop) {
        StringBuilder sb = new StringBuilder();
        sb.append("while (").append(whileLoop.getCondition().accept(this)).append(") {\n");
        for (Statement stmt : whileLoop.getBody()) {
            sb.append("            ").append(stmt.accept(this)).append("\n");
        }
        sb.append("        }");
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
