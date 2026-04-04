package com.microbench.ast;

public class LiteralExpr implements Expression {
    private final String value;
    private final String type;

    public LiteralExpr(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
