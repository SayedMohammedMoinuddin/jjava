package com.microbench.ast;

public class PrintStmt implements Statement {
    private final Expression expr;

    public PrintStmt(Expression expr) {
        this.expr = expr;
    }

    public Expression getExpr() {
        return expr;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
