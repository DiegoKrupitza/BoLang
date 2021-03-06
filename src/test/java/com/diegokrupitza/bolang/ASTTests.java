package com.diegokrupitza.bolang;

import com.diegokrupitza.bolang.syntaxtree.BuildAstException;
import com.diegokrupitza.bolang.syntaxtree.BuildAstVisitor;
import com.diegokrupitza.bolang.syntaxtree.nodes.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.data.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.infix.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.stat.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.unary.NegateNode;
import com.diegokrupitza.pdfgenerator.BoLexer;
import com.diegokrupitza.pdfgenerator.BoParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 08.07.21
 */
public class ASTTests {

    @Test
    public void simpleReturnNumVal() {
        var logLines = "return 5.1;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(DoubleNode.class);
        assertThat(((DoubleNode) ((ReturnNode) head.getStats().get(0)).getRet()).getValue()).isEqualTo(5.1);
    }

    @Test
    public void simpleExprForwardTest() {
        var logLines = "Sys.print(\"Hey You!\");";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(CallFunctionNode.class);
        assertThat(((CallFunctionNode) head.getStats().get(0)).getModule()).isEqualTo("Sys");
        assertThat(((CallFunctionNode) head.getStats().get(0)).getName()).isEqualTo("print");
        assertThat(((CallFunctionNode) head.getStats().get(0)).getParams()).hasSize(1).containsExactly(new StringNode("Hey You!"));
    }

    @Test
    public void simpleReturnStringVal() {
        var logLines = "return \"Hello World\";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(StringNode.class);
        assertThat(((StringNode) ((ReturnNode) head.getStats().get(0)).getRet()).getValue()).isEqualTo("Hello World");
    }

    @Test
    public void simpleReturnId() {
        var logLines = "var testVar := 1; return testVar;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(2);
        assertThat(head.getStats().get(1)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(1)).getRet()).isInstanceOf(IdNode.class);
        assertThat(((IdNode) ((ReturnNode) head.getStats().get(1)).getRet()).getName()).isEqualTo("testVar");
    }

    @Test
    public void simpleReturnFunctionCall() {
        var logLines = "return Date.date();";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(CallFunctionNode.class);
        assertThat(((CallFunctionNode) ((ReturnNode) head.getStats().get(0)).getRet()).getName()).isEqualTo("date");
        assertThat(((CallFunctionNode) ((ReturnNode) head.getStats().get(0)).getRet()).getModule()).isEqualTo("Date");
    }

    @Test
    public void simpleReturnFunctionCallWithParams() {
        var logLines = "return Date.date(1,2,3);";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        ExpressionNode ret = ((ReturnNode) head.getStats().get(0)).getRet();
        CallFunctionNode callFunctionNode = (CallFunctionNode) ret;

        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(ret).isInstanceOf(CallFunctionNode.class);
        assertThat(callFunctionNode.getName()).isEqualTo("date");
        assertThat(callFunctionNode.getModule()).isEqualTo("Date");
        assertThat(callFunctionNode.getParams().size()).isEqualTo(3);
        assertThat(callFunctionNode.getParams()).isEqualTo(Arrays.asList(new IntegerNode(1), new IntegerNode(2), new IntegerNode(3)));
    }

    @Test
    public void simpleReturnAddition() {

        double leftVal = 4;
        double rightVal = 5;

        var logLines = "return " + leftVal + " + " + rightVal + ";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(AdditionNode.class);

        // casting to operation node
        AdditionNode operationNode = (AdditionNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(operationNode.getLeft()).isInstanceOf(DoubleNode.class);
        assertThat(operationNode.getRight()).isInstanceOf(DoubleNode.class);

        assertThat(((DoubleNode) operationNode.getLeft()).getValue()).isEqualTo(leftVal);
        assertThat(((DoubleNode) operationNode.getRight()).getValue()).isEqualTo(rightVal);
    }

    @Test
    public void simpleReturnSubtraction() {

        double leftVal = 4;
        double rightVal = 5;

        var logLines = "return " + leftVal + " - " + rightVal + ";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(SubtractionNode.class);

        // casting to operation node
        SubtractionNode operationNode = (SubtractionNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(operationNode.getLeft()).isInstanceOf(DoubleNode.class);
        assertThat(operationNode.getRight()).isInstanceOf(DoubleNode.class);

        assertThat(((DoubleNode) operationNode.getLeft()).getValue()).isEqualTo(leftVal);
        assertThat(((DoubleNode) operationNode.getRight()).getValue()).isEqualTo(rightVal);
    }

    @Test
    public void simpleReturnMulti() {

        double leftVal = 4;
        double rightVal = 5;

        var logLines = "return " + leftVal + " * " + rightVal + ";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(MultiplicationNode.class);

        // casting to operation node
        MultiplicationNode operationNode = (MultiplicationNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(operationNode.getLeft()).isInstanceOf(DoubleNode.class);
        assertThat(operationNode.getRight()).isInstanceOf(DoubleNode.class);

        assertThat(((DoubleNode) operationNode.getLeft()).getValue()).isEqualTo(leftVal);
        assertThat(((DoubleNode) operationNode.getRight()).getValue()).isEqualTo(rightVal);
    }

    @Test
    public void simpleReturnDiv() {

        double leftVal = 4;
        double rightVal = 5;

        var logLines = "return " + leftVal + " / " + rightVal + ";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(DivisionNode.class);

        // casting to operation node
        DivisionNode operationNode = (DivisionNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(operationNode.getLeft()).isInstanceOf(DoubleNode.class);
        assertThat(operationNode.getRight()).isInstanceOf(DoubleNode.class);

        assertThat(((DoubleNode) operationNode.getLeft()).getValue()).isEqualTo(leftVal);
        assertThat(((DoubleNode) operationNode.getRight()).getValue()).isEqualTo(rightVal);
    }

    @Test
    public void simpleReturnLess() {

        double leftVal = 4;
        double rightVal = 5;

        var logLines = "return " + leftVal + " < " + rightVal + ";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(LessNode.class);

        // casting to operation node
        LessNode operationNode = (LessNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(operationNode.getLeft()).isInstanceOf(DoubleNode.class);
        assertThat(operationNode.getRight()).isInstanceOf(DoubleNode.class);

        assertThat(((DoubleNode) operationNode.getLeft()).getValue()).isEqualTo(leftVal);
        assertThat(((DoubleNode) operationNode.getRight()).getValue()).isEqualTo(rightVal);
    }

    @Test
    public void simpleReturnLessEqual() {

        double leftVal = 4;
        double rightVal = 5;

        var logLines = "return " + leftVal + " <= " + rightVal + ";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(LessEqualNode.class);

        // casting to operation node
        LessEqualNode operationNode = (LessEqualNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(operationNode.getLeft()).isInstanceOf(DoubleNode.class);
        assertThat(operationNode.getRight()).isInstanceOf(DoubleNode.class);

        assertThat(((DoubleNode) operationNode.getLeft()).getValue()).isEqualTo(leftVal);
        assertThat(((DoubleNode) operationNode.getRight()).getValue()).isEqualTo(rightVal);
    }

    @Test
    public void simpleReturnGreater() {

        double leftVal = 4;
        double rightVal = 5;

        var logLines = "return " + leftVal + " > " + rightVal + ";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(GreaterNode.class);

        // casting to operation node
        GreaterNode operationNode = (GreaterNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(operationNode.getLeft()).isInstanceOf(DoubleNode.class);
        assertThat(operationNode.getRight()).isInstanceOf(DoubleNode.class);

        assertThat(((DoubleNode) operationNode.getLeft()).getValue()).isEqualTo(leftVal);
        assertThat(((DoubleNode) operationNode.getRight()).getValue()).isEqualTo(rightVal);
    }

    @Test
    public void simpleReturnGreaterEqual() {

        double leftVal = 4;
        double rightVal = 5;

        var logLines = "return " + leftVal + " >= " + rightVal + ";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(GreaterEqualNode.class);

        // casting to operation node
        GreaterEqualNode operationNode = (GreaterEqualNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(operationNode.getLeft()).isInstanceOf(DoubleNode.class);
        assertThat(operationNode.getRight()).isInstanceOf(DoubleNode.class);

        assertThat(((DoubleNode) operationNode.getLeft()).getValue()).isEqualTo(leftVal);
        assertThat(((DoubleNode) operationNode.getRight()).getValue()).isEqualTo(rightVal);
    }

    @Test
    public void simpleReturnEqual() {

        double leftVal = 4;
        double rightVal = 5;

        var logLines = "return " + leftVal + " == " + rightVal + ";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(EqualsNode.class);

        // casting to operation node
        EqualsNode operationNode = (EqualsNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(operationNode.getLeft()).isInstanceOf(DoubleNode.class);
        assertThat(operationNode.getRight()).isInstanceOf(DoubleNode.class);

        assertThat(((DoubleNode) operationNode.getLeft()).getValue()).isEqualTo(leftVal);
        assertThat(((DoubleNode) operationNode.getRight()).getValue()).isEqualTo(rightVal);
    }

    @Test
    public void simpleReturnNotEqual() {

        double leftVal = 4;
        double rightVal = 5;

        var logLines = "return " + leftVal + " != " + rightVal + ";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(NotEqualsNode.class);

        // casting to operation node
        NotEqualsNode operationNode = (NotEqualsNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(operationNode.getLeft()).isInstanceOf(DoubleNode.class);
        assertThat(operationNode.getRight()).isInstanceOf(DoubleNode.class);

        assertThat(((DoubleNode) operationNode.getLeft()).getValue()).isEqualTo(leftVal);
        assertThat(((DoubleNode) operationNode.getRight()).getValue()).isEqualTo(rightVal);
    }

    @Test
    public void simpleReturnLogicAnd() {

        double leftVal = 4;
        double rightVal = 5;

        var logLines = "return " + leftVal + " && " + rightVal + ";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(LogicAndNode.class);

        // casting to operation node
        LogicAndNode operationNode = (LogicAndNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(operationNode.getLeft()).isInstanceOf(DoubleNode.class);
        assertThat(operationNode.getRight()).isInstanceOf(DoubleNode.class);

        assertThat(((DoubleNode) operationNode.getLeft()).getValue()).isEqualTo(leftVal);
        assertThat(((DoubleNode) operationNode.getRight()).getValue()).isEqualTo(rightVal);
    }

    @Test
    public void simpleReturnLogicOr() {

        double leftVal = 4;
        double rightVal = 5;

        var logLines = "return " + leftVal + " || " + rightVal + ";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(LogicOrNode.class);

        // casting to operation node
        LogicOrNode operationNode = (LogicOrNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(operationNode.getLeft()).isInstanceOf(DoubleNode.class);
        assertThat(operationNode.getRight()).isInstanceOf(DoubleNode.class);

        assertThat(((DoubleNode) operationNode.getLeft()).getValue()).isEqualTo(leftVal);
        assertThat(((DoubleNode) operationNode.getRight()).getValue()).isEqualTo(rightVal);
    }

    @Test
    public void simpleReturnConcat() {

        double leftVal = 4;
        double rightVal = 5;

        var logLines = "return " + leftVal + " ++ " + rightVal + ";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(StringConcatenationNode.class);

        // casting to operation node
        StringConcatenationNode operationNode = (StringConcatenationNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(operationNode.getLeft()).isInstanceOf(DoubleNode.class);
        assertThat(operationNode.getRight()).isInstanceOf(DoubleNode.class);

        assertThat(((DoubleNode) operationNode.getLeft()).getValue()).isEqualTo(leftVal);
        assertThat(((DoubleNode) operationNode.getRight()).getValue()).isEqualTo(rightVal);
    }

    @Test
    public void simpleVarDef() {
        var logLines = "var testVar := 5;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(DefineVariableNode.class);
        assertThat(((DefineVariableNode) head.getStats().get(0)).getIdentifierName()).isEqualTo("testVar");
        assertThat(((DefineVariableNode) head.getStats().get(0)).getExpr()).isInstanceOf(IntegerNode.class);
        assertThat(((IntegerNode) (((DefineVariableNode) head.getStats().get(0)).getExpr())).getValue()).isEqualTo(5);
    }

    @Test
    public void simpleVarAssign() {
        var logLines = "var testVar := 0; testVar := \"Super duper cool!\";";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);


        assertThat(head.getStats().size()).isEqualTo(2);
        assertThat(head.getStats().get(1)).isInstanceOf(AssignVariableNode.class);
        assertThat(((AssignVariableNode) head.getStats().get(1)).getIdentifierName()).isEqualTo("testVar");
        assertThat(((AssignVariableNode) head.getStats().get(1)).getExpr()).isInstanceOf(StringNode.class);
        assertThat(((StringNode) (((AssignVariableNode) head.getStats().get(1)).getExpr())).getValue()).isEqualTo("Super duper cool!");
    }

    @Test
    public void simpleValidScopeChecks() {
        var logLines = "var test := 1; return test;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);
    }

    @Test
    public void moreComplexValidScopeChecks() {
        var logLines = "var test := 1;" +
                "if(test == 1) {" +
                "var f := 4;" +
                "test := f;" +
                "f := 2;" +
                "}" +
                "else {" +
                "var f := 5;" +
                "test := f - 5;" +
                "f := 0;" +
                "}" +
                "return test;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats().size()).isEqualTo(3);

        EqualsNode condition = (EqualsNode) ((IfElseNode) head.getStats().get(1)).getCondition();
        assertThat(((IdNode) condition.getLeft()).getName()).isEqualTo("test");
        assertThat(((IntegerNode) condition.getRight()).getValue()).isEqualTo(1);

        assertThat(((IfElseNode) head.getStats().get(1)).getIfSection().size()).isEqualTo(3);
        assertThat(((IfElseNode) head.getStats().get(1)).getElseSection().size()).isEqualTo(3);
    }

    @Test
    public void simpleIfStatsTest() {
        var logLines = "var test := 1;" +
                "if(test == 1) {" +
                "var f := 4;" +
                "test := f;" +
                "f := 2;" +
                "}" +
                "return test;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats().size()).isEqualTo(3);

        EqualsNode condition = (EqualsNode) ((IfNode) head.getStats().get(1)).getCondition();
        assertThat(((IdNode) condition.getLeft()).getName()).isEqualTo("test");
        assertThat(((IntegerNode) condition.getRight()).getValue()).isEqualTo(1);

        assertThat(((IfNode) head.getStats().get(1)).getIfSection().size()).isEqualTo(3);
    }

    @Test
    public void simpleReturnArrayTest() {
        var logLines = "return [1,2,(2 + 1),\"Test\"];";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats().size()).isEqualTo(1);

        ArrayNode arrayNode = (ArrayNode) ((ReturnNode) head.getStats().get(0)).getRet();
        assertThat(arrayNode.getContent().size()).isEqualTo(4);
        assertThat(((IntegerNode) arrayNode.getContent().get(0)).getValue()).isEqualTo(1);
        assertThat(((IntegerNode) arrayNode.getContent().get(1)).getValue()).isEqualTo(2);
        assertThat(((AdditionNode) arrayNode.getContent().get(2)).getLeft()).isInstanceOf(IntegerNode.class);
        assertThat(((AdditionNode) arrayNode.getContent().get(2)).getRight()).isInstanceOf(IntegerNode.class);
        assertThat(((StringNode) arrayNode.getContent().get(3)).getValue()).isEqualTo("Test");
    }

    @Test
    public void simpleAccessArrayTest() {
        var logLines = "var test := [1,2,(2 + 1),\"Test\"]; return test[1];";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats().size()).isEqualTo(2);

        ArrayNode arrayNode = (ArrayNode) ((DefineVariableNode) head.getStats().get(0)).getExpr();
        assertThat(arrayNode.getContent().size()).isEqualTo(4);
        assertThat(((IntegerNode) arrayNode.getContent().get(0)).getValue()).isEqualTo(1);
        assertThat(((IntegerNode) arrayNode.getContent().get(1)).getValue()).isEqualTo(2);
        assertThat(((AdditionNode) arrayNode.getContent().get(2)).getLeft()).isInstanceOf(IntegerNode.class);
        assertThat(((AdditionNode) arrayNode.getContent().get(2)).getRight()).isInstanceOf(IntegerNode.class);
        assertThat(((StringNode) arrayNode.getContent().get(3)).getValue()).isEqualTo("Test");

        AccessIndexNode accessIndexNode = (AccessIndexNode) ((ReturnNode) head.getStats().get(1)).getRet();
        assertThat(accessIndexNode.getIdentifierName()).isEqualTo("test");
        assertThat(((IntegerNode) accessIndexNode.getIndex()).getValue()).isEqualTo(1);
    }

    @Test
    public void complexAccessArrayTest() {
        var logLines = "var test := [1,2,(2 + 1),\"Test\"]; return test[(1-3)];";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats().size()).isEqualTo(2);

        ArrayNode arrayNode = (ArrayNode) ((DefineVariableNode) head.getStats().get(0)).getExpr();
        assertThat(arrayNode.getContent().size()).isEqualTo(4);
        assertThat(((IntegerNode) arrayNode.getContent().get(0)).getValue()).isEqualTo(1);
        assertThat(((IntegerNode) arrayNode.getContent().get(1)).getValue()).isEqualTo(2);
        assertThat(((AdditionNode) arrayNode.getContent().get(2)).getLeft()).isInstanceOf(IntegerNode.class);
        assertThat(((AdditionNode) arrayNode.getContent().get(2)).getRight()).isInstanceOf(IntegerNode.class);
        assertThat(((StringNode) arrayNode.getContent().get(3)).getValue()).isEqualTo("Test");

        AccessIndexNode accessIndexNode = (AccessIndexNode) ((ReturnNode) head.getStats().get(1)).getRet();
        assertThat(accessIndexNode.getIdentifierName()).isEqualTo("test");
        assertThat(((IntegerNode) ((SubtractionNode) accessIndexNode.getIndex()).getLeft()).getValue()).isEqualTo(1);
        assertThat(((IntegerNode) ((SubtractionNode) accessIndexNode.getIndex()).getRight()).getValue()).isEqualTo(3);
    }

    @Test
    public void simpleReturnUnaryTest() {
        var logLines = "return -1;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(NegateNode.class);

        NegateNode negateNode = (NegateNode) ((ReturnNode) head.getStats().get(0)).getRet();
        assertThat(((IntegerNode) negateNode.getInner()).getValue()).isEqualTo(1);
    }

    @Test
    public void simpleReturnNegBooleanTest() {
        var logLines = "return !True;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(NegateNode.class);

        NegateNode negateNode = (NegateNode) ((ReturnNode) head.getStats().get(0)).getRet();
        assertThat(((BooleanNode) negateNode.getInner()).getValue()).isTrue();
    }

    @Test
    public void simpleReturnNegBoolean2Test() {
        var logLines = "return !False;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(NegateNode.class);

        NegateNode negateNode = (NegateNode) ((ReturnNode) head.getStats().get(0)).getRet();
        assertThat(((BooleanNode) negateNode.getInner()).getValue()).isFalse();
    }

    @Test
    public void complexReturnUnaryTest() {
        var logLines = "return -1.1+1;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(AdditionNode.class);

        AdditionNode additionNode = (AdditionNode) ((ReturnNode) head.getStats().get(0)).getRet();


        assertThat(additionNode.getLeft()).isInstanceOf(NegateNode.class);
        assertThat(additionNode.getRight()).isInstanceOf(IntegerNode.class);

        assertThat(((DoubleNode) ((NegateNode) additionNode.getLeft()).getInner()).getValue()).isEqualTo(1.1);
        assertThat(((IntegerNode) (additionNode).getRight()).getValue()).isEqualTo(1);
    }

    @Test
    public void secondComplexReturnUnaryTest() {
        var logLines = "return -1.1-1;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(SubtractionNode.class);

        SubtractionNode subtractionNode = (SubtractionNode) ((ReturnNode) head.getStats().get(0)).getRet();


        assertThat(subtractionNode.getLeft()).isInstanceOf(NegateNode.class);
        assertThat(subtractionNode.getRight()).isInstanceOf(IntegerNode.class);

        assertThat(((DoubleNode) ((NegateNode) subtractionNode.getLeft()).getInner()).getValue()).isEqualTo(1.1);
        assertThat(((IntegerNode) (subtractionNode).getRight()).getValue()).isEqualTo(1);
    }

    @Test
    public void thirdComplexReturnUnaryTest() {
        var logLines = "return -1.1--1;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(((ReturnNode) head.getStats().get(0)).getRet()).isInstanceOf(SubtractionNode.class);

        SubtractionNode subtractionNode = (SubtractionNode) ((ReturnNode) head.getStats().get(0)).getRet();


        assertThat(subtractionNode.getLeft()).isInstanceOf(NegateNode.class);
        assertThat(subtractionNode.getRight()).isInstanceOf(NegateNode.class);

        assertThat(((DoubleNode) ((NegateNode) subtractionNode.getLeft()).getInner()).getValue()).isEqualTo(1.1);
        assertThat(((IntegerNode) ((NegateNode) subtractionNode.getRight()).getInner()).getValue()).isEqualTo(1);
    }

    @Test
    public void simpleReturnExternalParamTest() {
        var logLines = "return #name;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(logLines));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats().size()).isEqualTo(1);
        assertThat(head.getStats().get(0)).isInstanceOf(ReturnNode.class);
        ExternalParamNode paramNode = (ExternalParamNode) ((ReturnNode) head.getStats().get(0)).getRet();

        assertThat(paramNode.getName()).isEqualTo("name");
    }

    @Test
    public void simpleReturnBooleanTest() {
        var line = "return FALSE;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats()).isNotEmpty()
                .hasSize(1);

        ReturnNode returnNode = (ReturnNode) head.getStats().get(0);
        assertThat(returnNode.getRet()).isInstanceOf(BooleanNode.class);

        BooleanNode bool = (BooleanNode) returnNode.getRet();
        assertThat(bool.getValue()).isFalse();
    }

    @Test
    public void simpleReturnBooleanTest2() {
        var line = "return False;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats()).isNotEmpty()
                .hasSize(1);

        ReturnNode returnNode = (ReturnNode) head.getStats().get(0);
        assertThat(returnNode.getRet()).isInstanceOf(BooleanNode.class);

        BooleanNode bool = (BooleanNode) returnNode.getRet();
        assertThat(bool.getValue()).isFalse();
    }

    @Test
    public void simpleReturnBooleanTest3() {
        var line = "return false;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats()).isNotEmpty()
                .hasSize(1);

        ReturnNode returnNode = (ReturnNode) head.getStats().get(0);
        assertThat(returnNode.getRet()).isInstanceOf(BooleanNode.class);

        BooleanNode bool = (BooleanNode) returnNode.getRet();
        assertThat(bool.getValue()).isFalse();
    }

    @Test
    public void simpleReturnBooleanTest4() {
        var line = "return TRUE;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats()).isNotEmpty()
                .hasSize(1);

        ReturnNode returnNode = (ReturnNode) head.getStats().get(0);
        assertThat(returnNode.getRet()).isInstanceOf(BooleanNode.class);

        BooleanNode bool = (BooleanNode) returnNode.getRet();
        assertThat(bool.getValue()).isTrue();
    }

    @Test
    public void simpleReturnBooleanTest5() {
        var line = "return True;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats()).isNotEmpty()
                .hasSize(1);

        ReturnNode returnNode = (ReturnNode) head.getStats().get(0);
        assertThat(returnNode.getRet()).isInstanceOf(BooleanNode.class);

        BooleanNode bool = (BooleanNode) returnNode.getRet();
        assertThat(bool.getValue()).isTrue();
    }

    @Test
    public void simpleReturnBooleanTest6() {
        var line = "return true;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats()).isNotEmpty()
                .hasSize(1);

        ReturnNode returnNode = (ReturnNode) head.getStats().get(0);
        assertThat(returnNode.getRet()).isInstanceOf(BooleanNode.class);

        BooleanNode bool = (BooleanNode) returnNode.getRet();
        assertThat(bool.getValue()).isTrue();
    }

    @Test
    void customFunctionNoParamsTest() {
        var line = "function newFunc() {" +
                "   var x := 1;" +
                "   return x;" +
                "}";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats())
                .isNotNull()
                .hasSize(1);

        assertThat(head.getStats().get(0)).isInstanceOf(FunctionNode.class);
        assertThat(((FunctionNode) head.getStats().get(0)).getName()).isEqualTo("newFunc");
        assertThat(((FunctionNode) head.getStats().get(0)).getParamNames()).isNotNull().isEmpty();
        assertThat(((FunctionNode) head.getStats().get(0)).getBody()).isNotNull().hasSize(2);
    }

    @Test
    void customFunctionWithParamsTest() {
        var line = "function newFunc(a,b,c,d,e,f) {" +
                "   var x := 1;" +
                "   return x;" +
                "}";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats())
                .isNotNull()
                .hasSize(1);

        assertThat(head.getStats().get(0)).isInstanceOf(FunctionNode.class);
        assertThat(((FunctionNode) head.getStats().get(0)).getName()).isEqualTo("newFunc");
        assertThat(((FunctionNode) head.getStats().get(0)).getParamNames()).isNotNull().hasSize(6).containsExactly("a", "b", "c", "d", "e", "f");
        assertThat(((FunctionNode) head.getStats().get(0)).getBody()).isNotNull().hasSize(2);
    }

    @Test
    void customFunctionWithWrongParamsTest() {
        var line = "function newFunc(a,b,a) {" +
                "   var x := 1;" +
                "   return x;" +
                "}";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();

        assertThatThrownBy(() -> buildAstVisitor.visitBo(bo))
                .isInstanceOf(BuildAstException.class)
                .hasMessageContaining("The name of the parameters in the function `newFunc` have to be unique!");
    }

    @Test
    void customFunctionParamsScopeTest() {
        var line = "function newFunc(a,b,c,d,e,f) {" +
                "   var x := a + b + c;" +
                "   return x;" +
                "}";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats())
                .isNotNull()
                .hasSize(1);

        assertThat(head.getStats().get(0)).isInstanceOf(FunctionNode.class);
        assertThat(((FunctionNode) head.getStats().get(0)).getName()).isEqualTo("newFunc");
        assertThat(((FunctionNode) head.getStats().get(0)).getParamNames()).isNotNull().hasSize(6).containsExactly("a", "b", "c", "d", "e", "f");
        assertThat(((FunctionNode) head.getStats().get(0)).getBody()).isNotNull().hasSize(2);
    }

    @Test
    void customFunctionParamsInvalidScopeTest() {
        var line = "function newFunc(a,b,c) {" +
                "   var x := a + b + c + g;" +
                "   return x;" +
                "}";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        assertThatThrownBy(() -> buildAstVisitor.visitBo(bo))
                .isInstanceOf(BuildAstException.class)
                .hasMessageContaining("g not in scope");

    }

    @Test
    void customFunctionWithCallTest() {
        var line = "function newFunc() {" +
                "   var x := 1;" +
                "   return x;" +
                "}" +
                "" +
                "this.newFunc();";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats())
                .isNotNull()
                .hasSize(2);

        assertThat(head.getStats().get(0)).isInstanceOf(FunctionNode.class);
        assertThat(((FunctionNode) head.getStats().get(0)).getName()).isEqualTo("newFunc");
        assertThat(((FunctionNode) head.getStats().get(0)).getParamNames()).isNotNull().isEmpty();
        assertThat(((FunctionNode) head.getStats().get(0)).getBody()).isNotNull().hasSize(2);

        assertThat(head.getStats().get(1)).isInstanceOf(CallFunctionNode.class);
        assertThat(((CallFunctionNode) head.getStats().get(1)).getModule()).isEqualTo("this");
        assertThat(((CallFunctionNode) head.getStats().get(1)).getName()).isEqualTo("newFunc");
    }

    @Test
    void defineModuleValidTest() {
        var line = "module testModule;" +
                "" +
                "function foo(a,b) {" +
                "   return a + b;" +
                "}";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats()).isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(head.getStats().get(0)).isInstanceOf(ModuleNode.class);

        ModuleNode moduleNode = (ModuleNode) head.getStats().get(0);
        assertThat(moduleNode.getName()).isEqualTo("testModule");
        assertThat(moduleNode.getFunctions()).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(
                        new FunctionNode(
                                "foo",
                                List.of("a", "b"),
                                List.of(new ReturnNode(new AdditionNode(new IdNode("a"), new IdNode("b"))))));
    }

    @Test
    void defineEmptyModuleValidTest() {
        var line = "module testModule;" +
                "";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats()).isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(head.getStats().get(0)).isInstanceOf(ModuleNode.class);

        ModuleNode moduleNode = (ModuleNode) head.getStats().get(0);
        assertThat(moduleNode.getName()).isEqualTo("testModule");
        assertThat(moduleNode.getFunctions()).isNotNull()
                .isEmpty();
    }

    @Test
    void importModuleTest() {
        var line = "import test;" +
                "return test.foo();";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        assertThat(head.getStats())
                .isNotNull()
                .isNotEmpty()
                .hasSize(2);

        assertThat(head.getStats().get(0))
                .isInstanceOf(ImportNode.class);

        ImportNode importNode = (ImportNode) head.getStats().get(0);

        assertThat(importNode.getModuleName()).isEqualTo("test");

        assertThat(head.getStats().get(1))
                .isInstanceOf(ReturnNode.class);

        ReturnNode returnNode = (ReturnNode) head.getStats().get(1);
        assertThat(returnNode.getRet()).isInstanceOf(CallFunctionNode.class);

        assertThat(((CallFunctionNode) returnNode.getRet()).getName()).isEqualTo("foo");
        assertThat(((CallFunctionNode) returnNode.getRet()).getModule()).isEqualTo("test");
        assertThat(((CallFunctionNode) returnNode.getRet()).getParams()).isNotNull().isEmpty();

    }

    @Test
    void doubleImportModuleInvalidTest() {
        var line = "import test;" +
                "import test;" +
                "return test.foo();";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        assertThatThrownBy(() -> buildAstVisitor.visitBo(bo))
                .isInstanceOf(BuildAstException.class)
                .hasMessageContaining("You cannot import the module `test` multiple times!");

    }

    @Test
    void missingImportModuleInvalidTest() {
        var line = "return test.foo();";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        assertThatThrownBy(() -> buildAstVisitor.visitBo(bo))
                .isInstanceOf(BuildAstException.class)
                .hasMessageContaining("You have to import the module `test` before using it!");

    }

    @Test
    void selfImportThisInvalidTest() {
        var line = "import this;" +
                "return test.foo();";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        assertThatThrownBy(() -> buildAstVisitor.visitBo(bo))
                .isInstanceOf(BuildAstException.class)
                .hasMessageContaining("You cannot import yourself! Please remove the line `import this;`");
    }

    @Test
    void selfImportModuleInvalidTest() {
        var line = "module testModule;" +
                "import testModule;" +
                "" +
                "function foo(a,b) {" +
                "   return a + b;" +
                "}";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(line));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        assertThatThrownBy(() -> buildAstVisitor.visitBo(bo))
                .isInstanceOf(BuildAstException.class)
                .hasMessageContaining("You cannot import yourself! Please remove the line `import testModule;`");
    }
}
