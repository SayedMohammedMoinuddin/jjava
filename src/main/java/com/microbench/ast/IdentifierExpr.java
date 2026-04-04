package com.microbench.ast;

public class IdentifierExpr implements Expression {
    private final String name;

    public IdentifierExpr(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
