package com.diegokrupitza.bolang.vm.functions;

import com.diegokrupitza.bolang.syntaxtree.nodes.FunctionNode;
import com.diegokrupitza.bolang.vm.functions.exceptions.FunctionTableException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 03.08.21
 */
public class FunctionTable {

    private final List<FunctionNode> functions;

    public FunctionTable(List<FunctionNode> functions) throws FunctionTableException {
        assert functions != null;

        this.functions = new ArrayList<>();

        // add provided functions
        for (FunctionNode function : functions) {
            add(function);
        }
    }

    public void add(FunctionNode function) throws FunctionTableException {
        assert function != null : "The function to add is not allowed to be null!";
        assert functions != null : "Functions list is should never be null!";

        String funcName = function.getName();
        List<String> params = function.getParamNames();

        if (CollectionUtils.isEmpty(functions)) {
            // just add we dont have to perform checks
            this.functions.add(function);
            return;
        }

        // check if function with same signature is already defined
        List<FunctionNode> duplicatedFunctions = functions.stream()
                .filter(Predicate.isEqual(function))
                .collect(Collectors.toList());

        if (duplicatedFunctions.size() != 0) {
            throw new FunctionTableException(String.format("The function with the name %s and the params %s is already defined! Functions have to be unique in the combination of name and params (count and name)", funcName, params));
        }

        // finally adding
        this.functions.add(function);
    }

    public FunctionNode get(String functionName, int numberOfParams) throws FunctionTableException {
        assert functionName != null : "Function name is not allowed to be empty";

        // we can assure that we only have one in this
        // stream since `add` check that before insertion
        Optional<FunctionNode> optionalFunction = this.functions.stream()
                .filter(item -> functionName.equals(item.getName()))
                .filter(item -> item.getParamNames().size() == numberOfParams)
                .findFirst();

        return optionalFunction
                .orElseThrow(() ->
                        new FunctionTableException(
                                String.format("Cannot find the function %s with %d parameters", functionName, numberOfParams))
                );
    }


}
