package com.microbench.ast;

import java.util.List;

public class Program implements ASTNode {
    private final List<Statement> statements;

    public Program(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
