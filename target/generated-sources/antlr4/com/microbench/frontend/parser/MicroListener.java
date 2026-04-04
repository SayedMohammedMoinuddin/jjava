// Generated from com/microbench/frontend/parser/Micro.g4 by ANTLR 4.13.1
package com.microbench.frontend.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MicroParser}.
 */
public interface MicroListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MicroParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MicroParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MicroParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MicroParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MicroParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(MicroParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MicroParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(MicroParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MicroParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(MicroParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MicroParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(MicroParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MicroParser#assignStmt}.
	 * @param ctx the parse tree
	 */
	void enterAssignStmt(MicroParser.AssignStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MicroParser#assignStmt}.
	 * @param ctx the parse tree
	 */
	void exitAssignStmt(MicroParser.AssignStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MicroParser#printStmt}.
	 * @param ctx the parse tree
	 */
	void enterPrintStmt(MicroParser.PrintStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MicroParser#printStmt}.
	 * @param ctx the parse tree
	 */
	void exitPrintStmt(MicroParser.PrintStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MicroParser#forLoop}.
	 * @param ctx the parse tree
	 */
	void enterForLoop(MicroParser.ForLoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link MicroParser#forLoop}.
	 * @param ctx the parse tree
	 */
	void exitForLoop(MicroParser.ForLoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link MicroParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(MicroParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MicroParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(MicroParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code doubleExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterDoubleExpr(MicroParser.DoubleExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code doubleExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitDoubleExpr(MicroParser.DoubleExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eqExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterEqExpr(MicroParser.EqExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eqExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitEqExpr(MicroParser.EqExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code intExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIntExpr(MicroParser.IntExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIntExpr(MicroParser.IntExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code addSubExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddSubExpr(MicroParser.AddSubExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code addSubExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddSubExpr(MicroParser.AddSubExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bitAndExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBitAndExpr(MicroParser.BitAndExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bitAndExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBitAndExpr(MicroParser.BitAndExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterRelExpr(MicroParser.RelExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitRelExpr(MicroParser.RelExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mulDivExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMulDivExpr(MicroParser.MulDivExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mulDivExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMulDivExpr(MicroParser.MulDivExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParenExpr(MicroParser.ParenExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParenExpr(MicroParser.ParenExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code idExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIdExpr(MicroParser.IdExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code idExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIdExpr(MicroParser.IdExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code shiftExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterShiftExpr(MicroParser.ShiftExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code shiftExpr}
	 * labeled alternative in {@link MicroParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitShiftExpr(MicroParser.ShiftExprContext ctx);
}