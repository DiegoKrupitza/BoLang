package com.diegokrupitza.bolang.vm;

import com.diegokrupitza.bolang.syntaxtree.nodes.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.data.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.infix.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.stat.*;
import com.diegokrupitza.bolang.syntaxtree.nodes.unary.NegateNode;
import com.diegokrupitza.bolang.vm.functions.Function;
import com.diegokrupitza.bolang.vm.functions.FunctionFactory;
import com.diegokrupitza.bolang.vm.functions.FunctionTable;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionException;
import com.diegokrupitza.bolang.vm.functions.exceptions.FunctionTableException;
import com.diegokrupitza.bolang.vm.types.*;
import com.diegokrupitza.bolang.vm.utils.Arrays;
import com.diegokrupitza.bolang.vm.utils.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 09.07.21
 */
public class VirtualMachine {

    private BoNode programHead;

    private FunctionTable functionTable = new FunctionTable();
    private Map<String, AbstractElementType<?>> variables = new HashMap<>();
    private Map<String, String> externalParams = new HashMap<>();

    private AbstractElementType<?> returnedVal = null;

    public VirtualMachine(BoNode programHead) throws VirtualMachineException {
        this.programHead = programHead;
        buildFunctionTable();
    }

    public void addExternalModules(Map<String, List<FunctionNode>> modules) {
        functionTable.add(modules);
    }

    private void buildFunctionTable() throws FunctionTableException {
        assert this.programHead != null : "Program head should never be null at this stage!";

        // self defined functions
        List<FunctionNode> selfDefinedFunctions = this.programHead.getStats().stream()
                .filter(item -> item instanceof FunctionNode)
                .map(item -> ((FunctionNode) item))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(selfDefinedFunctions)) {
            // no functions defined means we do not have to build a custom function tabel
            return;
        }

        this.functionTable.add(Map.of("this", selfDefinedFunctions));
    }

    /**
     * Runs the program that was previously given to the constructor
     *
     * @param externalParams the external params that are given to the program
     * @return the return value of the program
     * @throws VirtualMachineException in case any error happens during runtime
     */
    public AbstractElementType<?> run(Map<String, String> externalParams) throws VirtualMachineException {
        // only update if there are external vars
        if (Objects.nonNull(externalParams)) {
            this.externalParams = externalParams;
        }

        // calling all the stats from the head node
        List<ExpressionNode> stats = this.programHead.getStats();
        if (processStats(stats)) {
            return this.returnedVal;
        }
        return null;
    }

    /**
     * Processes all the stats from a list
     *
     * @param stats the stats to process
     * @return <code>true</code> when the stats had a successful return otherwise <code>false</code>
     * @throws VirtualMachineException in case any error happens during runtime
     */
    private boolean processStats(List<ExpressionNode> stats) throws VirtualMachineException {
        for (ExpressionNode expressionNode : stats) {
            evalStat(expressionNode);
            if (this.returnedVal != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Processes a single stat
     *
     * @param currentNode the current stat node to process
     * @throws VirtualMachineException in case any error happens during runtime
     */
    private void evalStat(ExpressionNode currentNode) throws VirtualMachineException {
        if (currentNode instanceof DefineVariableNode) {

            DefineVariableNode defineVariableNode = (DefineVariableNode) currentNode;

            String varName = defineVariableNode.getIdentifierName();
            AbstractElementType<?> value = evalExpression(defineVariableNode.getExpr());

            // voids cannot be assigned to variables
            if (Types.isOfType(Type.VOID, value)) {
                throw new VirtualMachineException(String.format("You cannot assign a void to the variable %s", varName));
            }

            this.variables.put(varName, value);

        } else if (currentNode instanceof AssignVariableNode) {
            AssignVariableNode assignVariableNode = (AssignVariableNode) currentNode;

            String varName = assignVariableNode.getIdentifierName();
            if (!this.variables.containsKey(varName)) {
                throw new VirtualMachineException(String.format("Variable with the name `%s` does not exist!", varName));
            }

            AbstractElementType<?> value = evalExpression(assignVariableNode.getExpr());

            // voids cannot be assigned to variables
            if (Types.isOfType(Type.VOID, value)) {
                throw new VirtualMachineException(String.format("You cannot assign a void to the variable %s", varName));
            }

            this.variables.put(varName, value);

        } else if (currentNode instanceof IfElseNode) {
            IfElseNode ifElseNode = (IfElseNode) currentNode;

            AbstractElementType<?> evaluatedCondition = evalExpression(ifElseNode.getCondition());

            assert evaluatedCondition != null;
            if (!(evaluatedCondition instanceof BooleanElement)) {
                throw new VirtualMachineException(String.format("Conditions has to be of type boolean not %s!", evaluatedCondition.getType()));
            }

            BooleanElement evaluatedBooleanCondition = (BooleanElement) evaluatedCondition;

            if (Booleans.TRUE.equals(evaluatedBooleanCondition)) {
                // condition was true so call the if section
                List<ExpressionNode> ifSection = ifElseNode.getIfSection();
                boolean hasReturnVal = processStats(ifSection);
                return;
            } else {
                List<ExpressionNode> elseSection = ifElseNode.getElseSection();
                boolean hasReturnVal = processStats(elseSection);
                return;
            }


        } else if (currentNode instanceof IfNode) {
            IfNode ifNode = (IfNode) currentNode;

            AbstractElementType<?> evaluatedCondition = evalExpression(ifNode.getCondition());

            assert evaluatedCondition != null;
            if (!(evaluatedCondition instanceof BooleanElement)) {
                throw new VirtualMachineException(String.format("Conditions has to be of type boolean not %s!", evaluatedCondition.getType()));
            }

            BooleanElement evaluatedBooleanCondition = (BooleanElement) evaluatedCondition;

            if (Booleans.TRUE.equals(evaluatedBooleanCondition)) {
                List<ExpressionNode> ifSection = ifNode.getIfSection();
                boolean hasReturnVal = processStats(ifSection);
                return;
            }

        } else if (currentNode instanceof ReturnNode) {
            ReturnNode returnNode = (ReturnNode) currentNode;

            AbstractElementType<?> evaledExpr = evalExpression(returnNode.getRet());

            // voids cannot be assigned to variables
            if (Types.isOfType(Type.VOID, evaledExpr)) {
                throw new VirtualMachineException("You cannot return a void");
            }


            this.returnedVal = evaledExpr;
        } else if (currentNode instanceof FunctionNode) {
            // we skip that since its a definition of a function
        } else if (currentNode instanceof ImportNode) {
            // we skip that since its a import
        } else {
            evalExpression(currentNode);
        }
    }

    private AbstractElementType<?> evalExpression(ExpressionNode expr) throws VirtualMachineException {
        if (expr instanceof NegateNode) {
            NegateNode negateNode = (NegateNode) expr;
            AbstractElementType<?> evaluated = evalExpression(negateNode.getInner());

            return Unarys.performNegation(evaluated);
        } else if (expr instanceof AdditionNode) {
            AdditionNode additionNode = (AdditionNode) expr;

            // evaluating first the left part of the addition node since its a
            // infix notation and we make a left-right depth search
            ExpressionNode leftExpr = additionNode.getLeft();
            AbstractElementType<?> leftElemInfo = evalExpression(leftExpr);

            // now the right part
            ExpressionNode rightExpr = additionNode.getRight();
            AbstractElementType<?> rightElemInfo = evalExpression(rightExpr);

            return Infixes.performAddition(leftElemInfo, rightElemInfo);
        } else if (expr instanceof DivisionNode) {
            DivisionNode divisionNode = (DivisionNode) expr;

            // evaluating first the left part of the node since its a
            // infix notation and we make a left-right depth search
            ExpressionNode leftExpr = divisionNode.getLeft();
            AbstractElementType<?> leftElemInfo = evalExpression(leftExpr);

            // now the right part
            ExpressionNode rightExpr = divisionNode.getRight();
            AbstractElementType<?> rightElemInfo = evalExpression(rightExpr);

            return Infixes.performDivision(leftElemInfo, rightElemInfo);
        } else if (expr instanceof EqualsNode) {
            EqualsNode equalsNode = (EqualsNode) expr;

            // evaluating first the left part of the node since its a
            // infix notation and we make a left-right depth search
            ExpressionNode leftExpr = equalsNode.getLeft();
            AbstractElementType<?> leftElemInfo = evalExpression(leftExpr);

            // now the right part
            ExpressionNode rightExpr = equalsNode.getRight();
            AbstractElementType<?> rightElemInfo = evalExpression(rightExpr);

            return (leftElemInfo.equals(rightElemInfo)) ? Booleans.TRUE : Booleans.FALSE;
        } else if (expr instanceof NotEqualsNode) {
            NotEqualsNode notEqualsNode = (NotEqualsNode) expr;

            // evaluating first the left part of the node since its a
            // infix notation and we make a left-right depth search
            ExpressionNode leftExpr = notEqualsNode.getLeft();
            AbstractElementType<?> leftElemInfo = evalExpression(leftExpr);

            // now the right part
            ExpressionNode rightExpr = notEqualsNode.getRight();
            AbstractElementType<?> rightElemInfo = evalExpression(rightExpr);

            return (!leftElemInfo.equals(rightElemInfo)) ? Booleans.TRUE : Booleans.FALSE;
        } else if (expr instanceof GreaterEqualNode) {
            GreaterEqualNode greaterEqualNode = (GreaterEqualNode) expr;

            // evaluating first the left part of the node since its a
            // infix notation and we make a left-right depth search
            ExpressionNode leftExpr = greaterEqualNode.getLeft();
            AbstractElementType<?> leftElemInfo = evalExpression(leftExpr);

            // now the right part
            ExpressionNode rightExpr = greaterEqualNode.getRight();
            AbstractElementType<?> rightElemInfo = evalExpression(rightExpr);

            if (Types.atLeastOneNotOfTypes(java.util.Arrays.asList(Type.INTEGER_NUMBER, Type.DOUBLE), leftElemInfo, rightElemInfo)) {
                throw new VirtualMachineException(String.format("The `>=` operation only works with numbers or integers. You tried to use it on %s and %s", leftElemInfo.getType(), rightElemInfo.getType()));
            }

            if (leftElemInfo.getType() == Type.INTEGER_NUMBER) {
                IntegerElement leftCasted = (IntegerElement) leftElemInfo;
                if (rightElemInfo.getType() == Type.INTEGER_NUMBER) {
                    IntegerElement rightCasted = (IntegerElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) >= 0) ? Booleans.TRUE : Booleans.FALSE;
                } else {
                    DoubleElement rightCasted = (DoubleElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) >= 0) ? Booleans.TRUE : Booleans.FALSE;
                }
            } else {
                DoubleElement leftCasted = (DoubleElement) leftElemInfo;
                if (rightElemInfo.getType() == Type.INTEGER_NUMBER) {
                    IntegerElement rightCasted = (IntegerElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) >= 0) ? Booleans.TRUE : Booleans.FALSE;
                } else {
                    DoubleElement rightCasted = (DoubleElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) >= 0) ? Booleans.TRUE : Booleans.FALSE;
                }
            }
        } else if (expr instanceof GreaterNode) {
            GreaterNode greaterNode = (GreaterNode) expr;

            // evaluating first the left part of the node since its a
            // infix notation and we make a left-right depth search
            ExpressionNode leftExpr = greaterNode.getLeft();
            AbstractElementType<?> leftElemInfo = evalExpression(leftExpr);

            // now the right part
            ExpressionNode rightExpr = greaterNode.getRight();
            AbstractElementType<?> rightElemInfo = evalExpression(rightExpr);

            if (Types.atLeastOneNotOfTypes(java.util.Arrays.asList(Type.INTEGER_NUMBER, Type.DOUBLE), leftElemInfo, rightElemInfo)) {
                throw new VirtualMachineException(String.format("The `>` operation only works with numbers or integers. You tried to use it on %s and %s", leftElemInfo.getType(), rightElemInfo.getType()));
            }

            if (leftElemInfo.getType() == Type.INTEGER_NUMBER) {
                IntegerElement leftCasted = (IntegerElement) leftElemInfo;
                if (rightElemInfo.getType() == Type.INTEGER_NUMBER) {
                    IntegerElement rightCasted = (IntegerElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) >= 1) ? Booleans.TRUE : Booleans.FALSE;
                } else {
                    DoubleElement rightCasted = (DoubleElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) >= 1) ? Booleans.TRUE : Booleans.FALSE;
                }
            } else {
                DoubleElement leftCasted = (DoubleElement) leftElemInfo;
                if (rightElemInfo.getType() == Type.INTEGER_NUMBER) {
                    IntegerElement rightCasted = (IntegerElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) >= 1) ? Booleans.TRUE : Booleans.FALSE;
                } else {
                    DoubleElement rightCasted = (DoubleElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) >= 1) ? Booleans.TRUE : Booleans.FALSE;
                }
            }
        } else if (expr instanceof LessEqualNode) {
            LessEqualNode lessEqualNode = (LessEqualNode) expr;

            // evaluating first the left part of the node since its a
            // infix notation and we make a left-right depth search
            ExpressionNode leftExpr = lessEqualNode.getLeft();
            AbstractElementType<?> leftElemInfo = evalExpression(leftExpr);

            // now the right part
            ExpressionNode rightExpr = lessEqualNode.getRight();
            AbstractElementType<?> rightElemInfo = evalExpression(rightExpr);

            if (Types.atLeastOneNotOfTypes(java.util.Arrays.asList(Type.INTEGER_NUMBER, Type.DOUBLE), leftElemInfo, rightElemInfo)) {
                throw new VirtualMachineException(String.format("The `<=` operation only works with numbers or integers. You tried to use it on %s and %s", leftElemInfo.getType(), rightElemInfo.getType()));
            }

            if (leftElemInfo.getType() == Type.INTEGER_NUMBER) {
                IntegerElement leftCasted = (IntegerElement) leftElemInfo;
                if (rightElemInfo.getType() == Type.INTEGER_NUMBER) {
                    IntegerElement rightCasted = (IntegerElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) <= 0) ? Booleans.TRUE : Booleans.FALSE;
                } else {
                    DoubleElement rightCasted = (DoubleElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) <= 0) ? Booleans.TRUE : Booleans.FALSE;
                }
            } else {
                DoubleElement leftCasted = (DoubleElement) leftElemInfo;
                if (rightElemInfo.getType() == Type.INTEGER_NUMBER) {
                    IntegerElement rightCasted = (IntegerElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) <= 0) ? Booleans.TRUE : Booleans.FALSE;
                } else {
                    DoubleElement rightCasted = (DoubleElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) <= 0) ? Booleans.TRUE : Booleans.FALSE;
                }
            }
        } else if (expr instanceof LessNode) {
            LessNode lessNode = (LessNode) expr;

            // evaluating first the left part of the node since its a
            // infix notation and we make a left-right depth search
            ExpressionNode leftExpr = lessNode.getLeft();
            AbstractElementType<?> leftElemInfo = evalExpression(leftExpr);

            // now the right part
            ExpressionNode rightExpr = lessNode.getRight();
            AbstractElementType<?> rightElemInfo = evalExpression(rightExpr);

            if (Types.atLeastOneNotOfTypes(java.util.Arrays.asList(Type.INTEGER_NUMBER, Type.DOUBLE), leftElemInfo, rightElemInfo)) {
                throw new VirtualMachineException(String.format("The `<` operation only works with numbers or integers. You tried to use it on %s and %s", leftElemInfo.getType(), rightElemInfo.getType()));
            }

            if (leftElemInfo.getType() == Type.INTEGER_NUMBER) {
                IntegerElement leftCasted = (IntegerElement) leftElemInfo;
                if (rightElemInfo.getType() == Type.INTEGER_NUMBER) {
                    IntegerElement rightCasted = (IntegerElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) <= -1) ? Booleans.TRUE : Booleans.FALSE;
                } else {
                    DoubleElement rightCasted = (DoubleElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) <= -1) ? Booleans.TRUE : Booleans.FALSE;
                }
            } else {
                DoubleElement leftCasted = (DoubleElement) leftElemInfo;
                if (rightElemInfo.getType() == Type.INTEGER_NUMBER) {
                    IntegerElement rightCasted = (IntegerElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) <= -1) ? Booleans.TRUE : Booleans.FALSE;
                } else {
                    DoubleElement rightCasted = (DoubleElement) rightElemInfo;
                    return (leftCasted.compareTo(rightCasted.getValue()) <= -1) ? Booleans.TRUE : Booleans.FALSE;
                }
            }
        } else if (expr instanceof LogicAndNode) {
            LogicAndNode logicAndNode = (LogicAndNode) expr;

            // evaluating first the left part of the node since its a
            // infix notation and we make a left-right depth search
            ExpressionNode leftExpr = logicAndNode.getLeft();
            AbstractElementType<?> leftElemInfo = evalExpression(leftExpr);

            // now the right part
            ExpressionNode rightExpr = logicAndNode.getRight();
            AbstractElementType<?> rightElemInfo = evalExpression(rightExpr);

            // logic operators only work on booleans
            if (Types.atLeastOneNotOfTypes(Collections.singletonList(Type.BOOLEAN), leftElemInfo, rightElemInfo)) {
                throw new VirtualMachineException(String.format("The logic `and` operation only works with booleans! You want to perform the `and` operation on a %s and %s", leftElemInfo.getType(), rightElemInfo.getType()));
            }

            BooleanElement leftBoolean = (BooleanElement) leftElemInfo;
            BooleanElement rightBoolean = (BooleanElement) rightElemInfo;

            return (leftBoolean.getValue() && rightBoolean.getValue()) ? Booleans.TRUE : Booleans.FALSE;
        } else if (expr instanceof LogicOrNode) {
            LogicOrNode logicOrNode = (LogicOrNode) expr;

            // evaluating first the left part of the node since its a
            // infix notation and we make a left-right depth search
            ExpressionNode leftExpr = logicOrNode.getLeft();
            AbstractElementType<?> leftElemInfo = evalExpression(leftExpr);

            // now the right part
            ExpressionNode rightExpr = logicOrNode.getRight();
            AbstractElementType<?> rightElemInfo = evalExpression(rightExpr);

            // logic operators only work on booleans
            if (Types.atLeastOneNotOfTypes(Collections.singletonList(Type.BOOLEAN), leftElemInfo, rightElemInfo)) {
                throw new VirtualMachineException(String.format("The logic `or` operation only works with booleans! You want to perform the `or` operation on a %s and %s", leftElemInfo.getType(), rightElemInfo.getType()));
            }

            BooleanElement leftBoolean = (BooleanElement) leftElemInfo;
            BooleanElement rightBoolean = (BooleanElement) rightElemInfo;

            return (leftBoolean.getValue() || rightBoolean.getValue()) ? Booleans.TRUE : Booleans.FALSE;
        } else if (expr instanceof MultiplicationNode) {
            MultiplicationNode multiplicationNode = (MultiplicationNode) expr;

            // evaluating first the left part of the node since its a
            // infix notation and we make a left-right depth search
            ExpressionNode leftExpr = multiplicationNode.getLeft();
            AbstractElementType<?> leftElemInfo = evalExpression(leftExpr);

            // now the right part
            ExpressionNode rightExpr = multiplicationNode.getRight();
            AbstractElementType<?> rightElemInfo = evalExpression(rightExpr);

            return Infixes.performMultiplication(leftElemInfo, rightElemInfo);

        } else if (expr instanceof StringConcatenationNode) {
            StringConcatenationNode concatenationNode = (StringConcatenationNode) expr;

            // evaluating first the left part of the node since its a
            // infix notation and we make a left-right depth search
            ExpressionNode leftExpr = concatenationNode.getLeft();
            AbstractElementType<?> leftElemInfo = evalExpression(leftExpr);

            // now the right part
            ExpressionNode rightExpr = concatenationNode.getRight();
            AbstractElementType<?> rightElemInfo = evalExpression(rightExpr);

            return Infixes.performStringConcatenation(leftElemInfo, rightElemInfo);

        } else if (expr instanceof SubtractionNode) {
            SubtractionNode subtractionNode = (SubtractionNode) expr;

            // evaluating first the left part of the node since its a
            // infix notation and we make a left-right depth search
            ExpressionNode leftExpr = subtractionNode.getLeft();
            AbstractElementType<?> leftElemInfo = evalExpression(leftExpr);

            // now the right part
            ExpressionNode rightExpr = subtractionNode.getRight();
            AbstractElementType<?> rightElemInfo = evalExpression(rightExpr);

            return Infixes.performSubtraction(leftElemInfo, rightElemInfo);

        } else if (expr instanceof AccessIndexNode) {
            AccessIndexNode accessIndexNode = (AccessIndexNode) expr;

            if (!this.variables.containsKey(accessIndexNode.getIdentifierName())) {
                throw new VirtualMachineException(String.format("The variable with the name %s does not exist!", accessIndexNode.getIdentifierName()));
            }

            // current state of the variable we are accessing
            AbstractElementType<?> identifierElemInfo = this.variables.get(accessIndexNode.getIdentifierName());

            // check if state of var is an array or string since only those support index accesing
            if (Types.atLeastOneNotOfTypes(java.util.Arrays.asList(Type.ARRAY, Type.STRING), identifierElemInfo)) {
                throw new VirtualMachineException(String.format("You can not use index accessing on an element of type %s", identifierElemInfo.getType()));
            }

            // the evaluated index
            AbstractElementType<?> indexElemInfo = evalExpression(accessIndexNode.getIndex());

            // check whether the index is an Int or not
            // only int can be used for index referencing
            if (Types.atLeastOneNotOfTypes(Collections.singletonList(Type.INTEGER_NUMBER), indexElemInfo)) {
                var indefiniteArticle = (Types.atLeastOneOfType(java.util.Arrays.asList(Type.INTEGER_NUMBER, Type.ARRAY), identifierElemInfo)) ? "an" : "a";
                throw new VirtualMachineException(String.format("You can not use a %s to access an element of %s %s", indexElemInfo.getType(), indefiniteArticle, identifierElemInfo.getType()));
            }

            // index has to be int
            IntegerElement castedIndexElem = (IntegerElement) indexElemInfo;

            // accessing the element at given index
            if (identifierElemInfo.getType() == Type.ARRAY) {
                ArrayElement castedElemInfo = (ArrayElement) identifierElemInfo;
                return castedElemInfo.get(castedIndexElem.getValue());
            } else if (identifierElemInfo.getType() == Type.STRING) {
                StringElement castedElemInfo = (StringElement) identifierElemInfo;
                return castedElemInfo.get(castedIndexElem.getValue());
            }

            throw new VirtualMachineException("This should never happen!");
        } else if (expr instanceof CallFunctionNode) {
            CallFunctionNode callFunctionNode = (CallFunctionNode) expr;

            // evaluating the values of the params so we get the correct values for the params
            List<AbstractElementType<?>> evaledParams = new ArrayList<>();
            for (ExpressionNode param : callFunctionNode.getParams()) {
                evaledParams.add(evalExpression(param));
            }

            // check if it is not a predefined function by the BoLang
            if (!FunctionFactory.getAllPredefinedModules()
                    .contains(callFunctionNode.getModule())) {
                // its not a predefined function aka we should have it in our functiontable
                FunctionNode toCallFunctionNode = this.functionTable.get(callFunctionNode.getModule(), callFunctionNode.getName(), evaledParams.size());

                // function calls have their own variable scope means we have to move current variable state outside
                Map<String, AbstractElementType<?>> oldVars = this.variables;
                this.variables = new HashMap<>();

                // set the values for the params
                assert evaledParams.size() == toCallFunctionNode.getParamNames().size() : "Evaled params do not match the count of the params. Means we call the function with too little params";
                for (int i = 0; i < toCallFunctionNode.getParamNames().size(); i++) {
                    String paramName = toCallFunctionNode.getParamNames().get(i);
                    AbstractElementType<?> paramValue = evaledParams.get(i);

                    // setting the value for the param
                    this.variables.put(paramName, paramValue);
                }

                // evaluating the function call
                processStats(toCallFunctionNode.getBody());

                // reseting the variables to before scope
                this.variables = oldVars;

                // check if the function call produced a return value
                // if so hand it further otherwise its a void
                return Objects.requireNonNullElse(this.returnedVal, VoidElement.NO_VALUE);
            }

            // loading the function we want to use based on the function name
            // function names will be unique
            Function function;
            try {
                function = FunctionFactory.getFunction(callFunctionNode.getModule(), callFunctionNode.getName());
            } catch (BoFunctionException e) {
                throw new VirtualMachineException(e.getMessage());
            }


            // calling the function with the evaled params so we get the value and its return type
            AbstractElementType<?> functionVal = null;
            try {
                functionVal = function.call(evaledParams);
            } catch (BoFunctionException e) {
                throw new VirtualMachineException(e.getMessage());
            }
            return functionVal;

        } else if (expr instanceof ArrayNode) {
            ArrayNode arrayNode = (ArrayNode) expr;

            ArrayElement returnArray = Arrays.emptyArray();
            for (ExpressionNode expressionNode : arrayNode.getContent()) {
                AbstractElementType<?> element = evalExpression(expressionNode);
                returnArray.add(element);
            }

            return returnArray;
        } else if (expr instanceof ExternalParamNode) {
            // all params are strings the coder needs to convert them into other formates by hand
            ExternalParamNode externalParamNode = (ExternalParamNode) expr;

            if (!this.externalParams.containsKey(externalParamNode.getName())) {
                throw new VirtualMachineException(String.format("The external parameter `%s` you want to access does not exist!", externalParamNode.getName()));
            }

            String extParm = this.externalParams.get(externalParamNode.getName());
            return new StringElement(extParm);
        } else if (expr instanceof IdNode) {
            IdNode idNode = (IdNode) expr;
            if (!this.variables.containsKey(idNode.getName())) {
                throw new VirtualMachineException(String.format("The variable with the name %s does not exist!", idNode.getName()));
            }

            return this.variables.get(idNode.getName());
        } else if (expr instanceof DoubleNode) {
            DoubleNode doubleNode = (DoubleNode) expr;
            return new DoubleElement(doubleNode.getValue());
        } else if (expr instanceof IntegerNode) {
            IntegerNode integerNode = (IntegerNode) expr;
            return new IntegerElement(integerNode.getValue());
        } else if (expr instanceof StringNode) {
            StringNode stringNode = (StringNode) expr;
            return new StringElement(stringNode.getValue());
        } else if (expr instanceof BooleanNode) {
            BooleanNode booleanNode = (BooleanNode) expr;
            return new BooleanElement(booleanNode.getValue());
        }
        throw new VirtualMachineException("Contact the Bo Language administrator. Please include the code you run to reach this error! Code: 2");
    }

}
