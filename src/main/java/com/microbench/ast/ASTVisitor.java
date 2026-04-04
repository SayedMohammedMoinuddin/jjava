package com.microbench.ast;

public interface ASTVisitor<T> {
    T visit(Program program);
    T visit(VarDecl varDecl);
    T visit(AssignStmt assignStmt);
    T visit(PrintStmt printStmt);
    T visit(ForLoop forLoop);
    T visit(WhileLoop whileLoop);
    T visit(BinaryExpr binaryExpr);
    T visit(LiteralExpr literalExpr);
    T visit(IdentifierExpr identifierExpr);
}
