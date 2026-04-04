package com.microbench.ast;

import java.util.List;

public class ForLoop implements Statement {
    private final VarDecl init;
    private final Expression condition;
    private final String updateVar;
    private final Expression updateExpr;
    private final List<Statement> body;

    public ForLoop(VarDecl init, Expression condition, String updateVar, Expression updateExpr, List<Statement> body) {
        this.init = init;
        this.condition = condition;
        this.updateVar = updateVar;
        this.updateExpr = updateExpr;
        this.body = body;
    }

    public VarDecl getInit() {
        return init;
    }

    public Expression getCondition() {
        return condition;
    }

    public String getUpdateVar() {
        return updateVar;
    }

    public Expression getUpdateExpr() {
        return updateExpr;
    }

    public List<Statement> getBody() {
        return body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
