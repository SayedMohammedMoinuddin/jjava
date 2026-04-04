package com.microbench.ast;

public class AssignStmt implements Statement {
    private final String name;
    private final Expression expr;

    public AssignStmt(String name, Expression expr) {
        this.name = name;
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    public Expression getExpr() {
        return expr;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
