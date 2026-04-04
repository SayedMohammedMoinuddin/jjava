package com.microbench.frontend;

import com.microbench.ast.*;
import com.microbench.frontend.parser.MicroBaseVisitor;
import com.microbench.frontend.parser.MicroParser;

import java.util.List;
import java.util.stream.Collectors;

public class ASTBuilder extends MicroBaseVisitor<ASTNode> {

    @Override
    public ASTNode visitProgram(MicroParser.ProgramContext ctx) {
        List<Statement> statements = ctx.statement().stream()
                .map(stmt -> (Statement) visit(stmt))
                .collect(Collectors.toList());
        return new Program(statements);
    }

    @Override
    public ASTNode visitStatement(MicroParser.StatementContext ctx) {
        if (ctx.varDecl() != null) return visit(ctx.varDecl());
        if (ctx.assignStmt() != null) return visit(ctx.assignStmt());
        if (ctx.printStmt() != null) return visit(ctx.printStmt());
        if (ctx.forLoop() != null) return visit(ctx.forLoop());
        throw new RuntimeException("Unknown statement type");
    }

    @Override
    public ASTNode visitVarDecl(MicroParser.VarDeclContext ctx) {
        String type = ctx.type().getText();
        String name = ctx.ID().getText();
        Expression expr = (Expression) visit(ctx.expr());
        return new VarDecl(type, name, expr);
    }

    @Override
    public ASTNode visitAssignStmt(MicroParser.AssignStmtContext ctx) {
        String name = ctx.ID().getText();
        Expression expr = (Expression) visit(ctx.expr());
        return new AssignStmt(name, expr);
    }

    @Override
    public ASTNode visitPrintStmt(MicroParser.PrintStmtContext ctx) {
        Expression expr = (Expression) visit(ctx.expr());
        return new PrintStmt(expr);
    }

    @Override
    public ASTNode visitForLoop(MicroParser.ForLoopContext ctx) {
        VarDecl init = (VarDecl) visit(ctx.varDecl());
        Expression condition = (Expression) visit(ctx.expr(0));
        String updateVar = ctx.ID().getText();
        Expression updateExpr = (Expression) visit(ctx.expr(1));
        List<Statement> body = ctx.statement().stream()
                .map(stmt -> (Statement) visit(stmt))
                .collect(Collectors.toList());
        return new ForLoop(init, condition, updateVar, updateExpr, body);
    }

    @Override
    public ASTNode visitMulDivExpr(MicroParser.MulDivExprContext ctx) {
        return createBinaryExpr(ctx.expr(0), ctx.getChild(1).getText(), ctx.expr(1));
    }

    @Override
    public ASTNode visitAddSubExpr(MicroParser.AddSubExprContext ctx) {
        return createBinaryExpr(ctx.expr(0), ctx.getChild(1).getText(), ctx.expr(1));
    }

    @Override
    public ASTNode visitShiftExpr(MicroParser.ShiftExprContext ctx) {
        return createBinaryExpr(ctx.expr(0), ctx.getChild(1).getText(), ctx.expr(1));
    }

    @Override
    public ASTNode visitBitAndExpr(MicroParser.BitAndExprContext ctx) {
        return createBinaryExpr(ctx.expr(0), ctx.getChild(1).getText(), ctx.expr(1));
    }

    @Override
    public ASTNode visitRelExpr(MicroParser.RelExprContext ctx) {
        return createBinaryExpr(ctx.expr(0), ctx.getChild(1).getText(), ctx.expr(1));
    }

    @Override
    public ASTNode visitEqExpr(MicroParser.EqExprContext ctx) {
        return createBinaryExpr(ctx.expr(0), ctx.getChild(1).getText(), ctx.expr(1));
    }

    private BinaryExpr createBinaryExpr(MicroParser.ExprContext leftCtx, String operator, MicroParser.ExprContext rightCtx) {
        Expression left = (Expression) visit(leftCtx);
        Expression right = (Expression) visit(rightCtx);
        return new BinaryExpr(left, operator, right);
    }

    @Override
    public ASTNode visitIdExpr(MicroParser.IdExprContext ctx) {
        return new IdentifierExpr(ctx.ID().getText());
    }

    @Override
    public ASTNode visitIntExpr(MicroParser.IntExprContext ctx) {
        return new LiteralExpr(ctx.INT_LIT().getText(), "int");
    }

    @Override
    public ASTNode visitDoubleExpr(MicroParser.DoubleExprContext ctx) {
        return new LiteralExpr(ctx.DOUBLE_LIT().getText(), "double");
    }

    @Override
    public ASTNode visitParenExpr(MicroParser.ParenExprContext ctx) {
        return visit(ctx.expr());
    }
}
