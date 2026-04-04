package com.microbench.ast;

import java.util.List;

public class WhileLoop implements Statement {
    private final Expression condition;
    private final List<Statement> body;

    public WhileLoop(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Statement> getBody() {
        return body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
