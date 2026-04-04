// Generated from com/microbench/frontend/parser/Micro.g4 by ANTLR 4.13.1
package com.microbench.frontend.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MicroParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MicroVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MicroParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MicroParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MicroParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(MicroParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MicroParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(MicroParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MicroParser#assignStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStmt(MicroParser.AssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MicroParser#printStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintStmt(MicroParser.PrintStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MicroParser#forLoop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForLoop(MicroParser.ForLoopContext ctx);
	/**
	 * Visit a parse tree produced by {@link MicroParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(MicroParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code doubleExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoubleExpr(MicroParser.DoubleExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eqExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqExpr(MicroParser.EqExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code intExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntExpr(MicroParser.IntExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code addSubExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddSubExpr(MicroParser.AddSubExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bitAndExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitAndExpr(MicroParser.BitAndExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code relExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelExpr(MicroParser.RelExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mulDivExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulDivExpr(MicroParser.MulDivExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpr(MicroParser.ParenExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code idExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdExpr(MicroParser.IdExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code shiftExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShiftExpr(MicroParser.ShiftExprContext ctx);
}