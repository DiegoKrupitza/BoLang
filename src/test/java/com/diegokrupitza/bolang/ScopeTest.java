package com.diegokrupitza.bolang;

import com.diegokrupitza.pdfgenerator.BoLexer;
import com.diegokrupitza.pdfgenerator.BoParser;
import com.diegokrupitza.bolang.syntaxtree.BuildAstException;
import com.diegokrupitza.bolang.syntaxtree.BuildAstVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 18.07.21
 */
public class ScopeTest {

    @Test
    public void notInScopeTest() {
        var varName = "varNotExists";
        var logLines = String.format("return %s;", varName);

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();


        assertThatThrownBy(() -> buildAstVisitor.visitBo(bo))
                .isInstanceOf(BuildAstException.class)
                .hasMessageContaining(String.format("Variable %s not in scope", varName));
    }

    @Test
    public void notInScopeAssignTest() {
        var varName = "varNotExists";
        var logLines = String.format("%s := 1;" +
                "return %s;", varName, varName);

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();


        assertThatThrownBy(() -> buildAstVisitor.visitBo(bo))
                .isInstanceOf(BuildAstException.class)
                .hasMessageContaining(String.format("Variable %s not in scope", varName));
    }

    @Test
    public void notInScopeStringArrayAccessTest() {
        var varName = "varNotExists";
        var logLines = String.format("return %s[0];", varName);

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();


        assertThatThrownBy(() -> buildAstVisitor.visitBo(bo))
                .isInstanceOf(BuildAstException.class)
                .hasMessageContaining(String.format("Variable %s not in scope", varName));
    }

    @Test
    public void doubleInScopeTest() {
        var varName = "doubleDef";
        var logLines = String.format("var %s := 1;" +
                "var %s := 1;" +
                "return %s;", varName, varName, varName);

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();


        assertThatThrownBy(() -> buildAstVisitor.visitBo(bo))
                .isInstanceOf(BuildAstException.class)
                .hasMessageContaining(String.format("The var %s is already defined!", varName));
    }


}
