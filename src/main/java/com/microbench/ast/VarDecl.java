package com.microbench.ast;

public class VarDecl implements Statement {
    private final String type;
    private final String name;
    private final Expression expr;

    public VarDecl(String type, String name, Expression expr) {
        this.type = type;
        this.name = name;
        this.expr = expr;
    }

    public String getType() {
        return type;
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
