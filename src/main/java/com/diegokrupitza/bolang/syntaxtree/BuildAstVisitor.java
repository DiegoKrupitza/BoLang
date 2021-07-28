package com.diegokrupitza.bolang.syntaxtree;

import com.diegokrupitza.pdfgenerator.BoBaseVisitor;
import com.diegokrupitza.pdfgenerator.BoLexer;
import com.diegokrupitza.pdfgenerator.BoParser;
import com.diegokrupitza.bolang.symboltable.BoSymbolTable;
import com.diegokrupitza.bolang.syntaxtree.nodes.AccessIndexNode;
import com.diegokrupitza.bolang.syntaxtree.nodes.BoNode;
import com.diegokrupitza.bolang.syntaxtree.nodes.ExpressionNode;
import com.diegokrupitza.bolang.syntaxtree.nodes.FunctionNode;
import com.diegokrupitza.bolang.syntaxtree.nodes.data.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.infix.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.stat.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.unary.NegateNode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 08.07.21
 */
public class BuildAstVisitor extends BoBaseVisitor<ExpressionNode> {

    private BoSymbolTable symbolTable = null;

    @Override
    public ExpressionNode visitBo(BoParser.BoContext ctx) {

        // create a global symbol table context
        this.symbolTable = new BoSymbolTable();

        // when we have multiple stat then this has multiple entries
        List<ExpressionNode> processesStats = ctx.children.stream()
                .map(this::visit)
                .collect(Collectors.toList());

        return new BoNode(processesStats);
    }

    @Override
    public ExpressionNode visitReturnStat(BoParser.ReturnStatContext ctx) {
        ExpressionNode visitedExpresion = visit(ctx.expr());
        return new ReturnNode(visitedExpresion);
    }

    @Override
    public ExpressionNode visitNumVal(BoParser.NumValContext ctx) {
        if (ctx.num.getText().contains(".")) {
            Double value = Double.valueOf(ctx.num.getText());
            return new DoubleNode(value);
        }
        Integer value = Integer.valueOf(ctx.num.getText());
        return new IntegerNode(value);

    }

    @Override
    public ExpressionNode visitStringVal(BoParser.StringValContext ctx) {
        String stringWithQuotes = ctx.string().getText();
        String text = stringWithQuotes.substring(1, stringWithQuotes.length() - 1);
        return new StringNode(text);
    }

    @Override
    public ExpressionNode visitIdentifierVal(BoParser.IdentifierValContext ctx) {
        String id = ctx.id.getText();

        boolean inScope = this.symbolTable.inScope(id);
        if (!inScope) {
            throw new BuildAstException(String.format("Variable %s not in scope", id));
        }

        return new IdNode(id);
    }

    @Override
    public ExpressionNode visitFuncVal(BoParser.FuncValContext ctx) {
        String funcName = ctx.func.getText();
        List<ExpressionNode> params = new ArrayList<>();

        // process possible params
        if (CollectionUtils.isNotEmpty(ctx.expr())) {
            params = ctx.expr().stream()
                    .map(this::visit)
                    .collect(Collectors.toList());
        }

        return new FunctionNode(funcName, params);
    }

    @Override
    public ExpressionNode visitInfixExpr(BoParser.InfixExprContext ctx) {

        InfixNode node;

        switch (ctx.op.getType()) {
            case BoLexer.OP_ADD:
                node = new AdditionNode();
                break;
            case BoLexer.OP_SUB:
                node = new SubtractionNode();
                break;
            case BoLexer.OP_MUL:
                node = new MultiplicationNode();
                break;
            case BoLexer.OP_DIV:
                node = new DivisionNode();
                break;
            case BoLexer.OP_LESS:
                node = new LessNode();
                break;
            case BoLexer.OP_LESSEQ:
                node = new LessEqualNode();
                break;
            case BoLexer.OP_GREATER:
                node = new GreaterNode();
                break;
            case BoLexer.OP_GREATEREQ:
                node = new GreaterEqualNode();
                break;
            case BoLexer.OP_NOTEQUAL:
                node = new NotEqualsNode();
                break;
            case BoLexer.OP_EQUAL:
                node = new EqualsNode();
                break;
            case BoLexer.OP_AND:
                node = new LogicAndNode();
                break;
            case BoLexer.OP_OR:
                node = new LogicOrNode();
                break;
            case BoLexer.OP_CONCAT:
                node = new StringConcatenationNode();
                break;

            default:
                throw new RuntimeException("Yeah this should never happen!");
        }

        node.setLeft(visit(ctx.left));
        node.setRight(visit(ctx.right));

        return node;
    }

    @Override
    public ExpressionNode visitVarDefStat(BoParser.VarDefStatContext ctx) {
        // defining a new var
        String idName = ctx.ID().getText();
        ExpressionNode expressionNode = visit(ctx.expr());

        // check if the same var already exists. If so then we have to scream!
        if (this.symbolTable.inScope(idName)) {
            throw new BuildAstException(String.format("The var %s is already defined!", idName));
        }

        // add the new var to the scope
        this.symbolTable.add(idName);

        return new DefineVariableNode(idName, expressionNode);
    }

    @Override
    public ExpressionNode visitVarAssignStat(BoParser.VarAssignStatContext ctx) {
        // asigning a value to the variable
        String idName = ctx.ID().getText();
        ExpressionNode expressionNode = visit(ctx.expr());

        boolean inScope = this.symbolTable.inScope(idName);
        if (!inScope) {
            throw new BuildAstException(String.format("Variable %s not in scope", idName));
        }

        return new AssignVariableNode(idName, expressionNode);
    }

    @Override
    public ExpressionNode visitCreateList(BoParser.CreateListContext ctx) {
        List<ExpressionNode> elements = new ArrayList<>();

        // process possible params
        if (CollectionUtils.isNotEmpty(ctx.expr())) {
            elements = ctx.expr().stream()
                    .map(this::visit)
                    .collect(Collectors.toList());
        }

        return new ArrayNode(elements);
    }

    @Override
    public ExpressionNode visitAccessStringOrListItem(BoParser.AccessStringOrListItemContext ctx) {

        String idName = ctx.id.getText();
        ExpressionNode expresionForIndex = visit(ctx.index);

        boolean inScope = this.symbolTable.inScope(idName);
        if (!inScope) {
            throw new BuildAstException(String.format("Variable %s not in scope", idName));
        }

        return new AccessIndexNode(idName, expresionForIndex);
    }

    @Override
    public ExpressionNode visitIfElseStat(BoParser.IfElseStatContext ctx) {

        BoParser.ExprContext cond = ctx.cond;
        ExpressionNode conditionVisit = visit(cond);

        // create new context for the if stat
        BoSymbolTable oldSymbol = this.symbolTable;
        this.symbolTable = this.symbolTable.createScope();

        BoParser.ScopecontContext ifStat = ctx.ifStat;
        List<ExpressionNode> visitedIfStats = ifStat.children.stream()
                .map(this::visit)
                .collect(Collectors.toList());

        // reset to a new scope for else stat
        this.symbolTable = oldSymbol.createScope();

        BoParser.ScopecontContext elseStat = ctx.elseStat;
        List<ExpressionNode> visitedElseStats = elseStat.children.stream()
                .map(this::visit)
                .collect(Collectors.toList());


        this.symbolTable = oldSymbol;

        return new IfElseNode(conditionVisit, visitedIfStats, visitedElseStats);
    }

    @Override
    public ExpressionNode visitIfStat(BoParser.IfStatContext ctx) {

        BoParser.ExprContext cond = ctx.cond;
        ExpressionNode conditionVisit = visit(cond);

        // create new context for the if stat
        BoSymbolTable oldSymbol = this.symbolTable;
        this.symbolTable = this.symbolTable.createScope();

        BoParser.ScopecontContext ifStat = ctx.ifStat;
        List<ExpressionNode> visitedIfStats = ifStat.children.stream()
                .map(this::visit)
                .collect(Collectors.toList());

        this.symbolTable = oldSymbol;

        return new IfNode(conditionVisit, visitedIfStats);
    }

    @Override
    public ExpressionNode visitUnaryExpr(BoParser.UnaryExprContext ctx) {
        switch (ctx.op.getType()) {
            case BoLexer.OP_NEG:
                return new NegateNode(visit(ctx.expr()));
            case BoLexer.OP_SUB:
                return new NegateNode(visit(ctx.expr()));
            default:
                throw new RuntimeException("Yeah this should never happen!");
        }
    }

    @Override
    public ExpressionNode visitBooleanEntry(BoParser.BooleanEntryContext ctx) {

        String text = ctx.BOOLEAN().getText();
        if ("TRUE".equalsIgnoreCase(text)) {
            return new BooleanNode(true);
        } else if ("FALSE".equalsIgnoreCase(text)) {
            return new BooleanNode(false);
        }

        throw new RuntimeException("Yeah this should never happen!");

    }

    @Override
    public ExpressionNode visitExternalParamVal(BoParser.ExternalParamValContext ctx) {
        String name = ctx.id.getText();
        return new ExternalParamNode(name);
    }

    @Override
    public ExpressionNode visitTermExpr(BoParser.TermExprContext ctx) {
        return visit(ctx.term());
    }

    @Override
    public ExpressionNode visitParensExpr(BoParser.ParensExprContext ctx) {
        return visit(ctx.expr());
    }
}
