package com.microbench.frontend;

import com.microbench.ast.*;
import com.microbench.backend.CGenerator;
import com.microbench.backend.JavaGenerator;
import com.microbench.frontend.parser.MicroLexer;
import com.microbench.frontend.parser.MicroParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LanguageTest {

    private Program parse(String source) {
        MicroLexer lexer = new MicroLexer(CharStreams.fromString(source));
        MicroParser parser = new MicroParser(new CommonTokenStream(lexer));
        ASTBuilder builder = new ASTBuilder();
        return (Program) builder.visit(parser.program());
    }

    @Test
    public void testWhileLoopParsing() {
        String source = "int x = 0; while (x < 10) { x = x + 1; }";
        Program program = parse(source);

        assertEquals(2, program.getStatements().size());
        assertTrue(program.getStatements().get(1) instanceof WhileLoop);

        WhileLoop whileLoop = (WhileLoop) program.getStatements().get(1);
        assertTrue(whileLoop.getCondition() instanceof BinaryExpr);
        assertEquals(1, whileLoop.getBody().size());
        assertTrue(whileLoop.getBody().get(0) instanceof AssignStmt);
    }

    @Test
    public void testJavaCodeGeneration() {
        String source = "int x = 0; while (x < 10) { x = x + 1; }";
        Program program = parse(source);

        JavaGenerator generator = new JavaGenerator();
        String code = generator.visit(program);

        assertTrue(code.contains("while ((x < 10))"));
        assertTrue(code.contains("x = (x + 1);"));
    }

    @Test
    public void testCCodeGeneration() {
        String source = "int x = 0; while (x < 10) { x = x + 1; }";
        Program program = parse(source);

        CGenerator generator = new CGenerator();
        String code = generator.visit(program);

        assertTrue(code.contains("while ((x < 10))"));
        assertTrue(code.contains("x = (x + 1);"));
    }
}
