package com.microbench.ast;

public interface ASTNode {
    <T> T accept(ASTVisitor<T> visitor);
}
