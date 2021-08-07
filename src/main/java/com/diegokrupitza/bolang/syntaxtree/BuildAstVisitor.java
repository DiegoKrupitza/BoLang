package com.diegokrupitza.bolang.syntaxtree;

import com.diegokrupitza.bolang.symboltable.BoSymbolTable;
import com.diegokrupitza.bolang.syntaxtree.nodes.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.data.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.infix.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.stat.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.unary.NegateNode;
import com.diegokrupitza.bolang.vm.functions.FunctionFactory;
import com.diegokrupitza.pdfgenerator.BoBaseVisitor;
import com.diegokrupitza.pdfgenerator.BoLexer;
import com.diegokrupitza.pdfgenerator.BoParser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 08.07.21
 */
public class BuildAstVisitor extends BoBaseVisitor<ExpressionNode> {

    private final Set<String> usedModules = new HashSet<>();
    private BoSymbolTable symbolTable = null;
    private List<String> importedModules = new ArrayList<>();
    private String currentModuleName = "this";

    @Override
    public ExpressionNode visitNormalCode(BoParser.NormalCodeContext ctx) {
        // create a global symbol table context
        this.symbolTable = new BoSymbolTable();

        // when we have multiple stat then this has multiple entries
        List<ExpressionNode> processesStats = ctx.children.stream()
                .map(this::visit)
                .collect(Collectors.toList());

        return new BoNode(processesStats, usedModules);
    }

    @Override
    public ExpressionNode visitModuleDef(BoParser.ModuleDefContext ctx) {
        // create a global symbol table context
        this.symbolTable = new BoSymbolTable();

        String moduleName = ctx.moduleName.getText();

        // adding the own module to the scope of imported modules
        importedModules.add(moduleName);
        currentModuleName = moduleName;

        List<ImportNode> importsInModule = ctx.importDef()
                .stream()
                .map(this::visit)
                .map(item -> ((ImportNode) item))
                .collect(Collectors.toUnmodifiableList());

        List<FunctionNode> functions = ctx.userFunc().stream()
                .map(this::visit)
                .map(item -> ((FunctionNode) item))
                .collect(Collectors.toUnmodifiableList());

        ModuleNode moduleNode = new ModuleNode(moduleName, importsInModule, functions);

        return new BoNode(List.of(moduleNode), usedModules);
    }

    @Override
    public ExpressionNode visitImportDefinition(BoParser.ImportDefinitionContext ctx) {
        String moduleName = ctx.moduleName.getText();

        if (currentModuleName.equals(moduleName)) {
            throw new BuildAstException(String.format("You cannot import yourself! Please remove the line `import %s;`", moduleName));
        }

        if (importedModules.contains(moduleName)) {
            throw new BuildAstException(String.format("You cannot import the module `%s` multiple times!", moduleName));
        }

        importedModules.add(moduleName);

        return new ImportNode(moduleName);
    }

    @Override
    public ExpressionNode visitBo(BoParser.BoContext ctx) {
        return visit(ctx.code());
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

        String moduleName = ctx.module.getText();

        if (!moduleName.equals("this") && // not this
                !FunctionFactory.getAllPredefinedModules().contains(moduleName) && // not a predefined
                !importedModules.contains(moduleName) // not already imported
        ) {
            throw new BuildAstException(String.format("You have to import the module `%s` before using it!", moduleName));
        }

        List<ExpressionNode> params = new ArrayList<>();

        // process possible params
        if (CollectionUtils.isNotEmpty(ctx.expr())) {
            params = ctx.expr().stream()
                    .map(this::visit)
                    .collect(Collectors.toList());
        }

        if (!FunctionFactory.getAllPredefinedModules().contains(moduleName)) {
            usedModules.add(moduleName);
        }

        return new CallFunctionNode(funcName, moduleName, params);
    }

    @Override
    public ExpressionNode visitWhileLoop(BoParser.WhileLoopContext ctx) {

        BoParser.ExprContext cond = ctx.cond;
        ExpressionNode conditionVisit = visit(cond);

        BoParser.ScopecontContext whileBody = ctx.whileBody;
        List<ExpressionNode> visitedWhileBody = whileBody.children.stream()
                .map(this::visit)
                .collect(Collectors.toList());

        return new WhileNode(conditionVisit, visitedWhileBody);
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
    public ExpressionNode visitUserFuncDef(BoParser.UserFuncDefContext ctx) {
        String funcName = ctx.funcName.getText();

        if (StringUtils.isEmpty(funcName)) {
            // Better exception
            throw new BuildAstException("You tried to define a function without giving it a name");
        }

        // params
        List<String> paramIdentifiers = ctx.ID().stream()
                .map(item -> item.getSymbol().getText())
                .filter(not(funcName::equals))
                .collect(Collectors.toList());

        // check if all params have a unique name
        if (paramIdentifiers.stream()
                .distinct()
                .count() != paramIdentifiers.size()) {
            // at least one param is not unique
            throw new BuildAstException(String.format("The name of the parameters in the function `%s` have to be unique!", funcName));
        }

        // create new context for the if stat
        BoSymbolTable oldSymbol = this.symbolTable;
        this.symbolTable = this.symbolTable.createScope();

        // adding the param to the function scope
        this.symbolTable.add(paramIdentifiers);

        BoParser.ScopecontContext functionBodyStat = ctx.funcStats;
        List<ExpressionNode> functionBody = functionBodyStat.children.stream()
                .map(this::visit)
                .collect(Collectors.toList());

        this.symbolTable = oldSymbol;
        return new FunctionNode(funcName, paramIdentifiers, functionBody);
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

    @Override
    public ExpressionNode visitExprForward(BoParser.ExprForwardContext ctx) {
        return visit(ctx.expr());
    }
}
