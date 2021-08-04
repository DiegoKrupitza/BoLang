package com.diegokrupitza.bolang.vm.functions;

import com.diegokrupitza.bolang.syntaxtree.nodes.FunctionNode;
import com.diegokrupitza.bolang.vm.functions.exceptions.FunctionTableException;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 03.08.21
 */
public class FunctionTable {

    // module name, list of function
    private final Map<String, List<FunctionNode>> functions = new HashMap<>();

    public void add(Map<String, List<FunctionNode>> functionsMap) {
        functionsMap.keySet()
                .forEach(item ->
                        this.functions.put(item, functionsMap.get(item))
                );
    }

    public void add(String module, FunctionNode function) throws FunctionTableException {
        assert module != null : "Module name is should never be null!";
        assert function != null : "The function to add is not allowed to be null!";

        String funcName = function.getName();
        List<String> params = function.getParamNames();

        if (this.functions.isEmpty()) {
            // just add we dont have to perform checks
            this.functions.put(module, new ArrayList<>(List.of(function)));
            return;
        }

        List<FunctionNode> functionsOfModule = this.functions.getOrDefault(module, new ArrayList<>());

        // check if function with same signature is already defined
        List<FunctionNode> duplicatedFunctions = functionsOfModule.stream()
                .filter(Predicate.isEqual(function))
                .collect(Collectors.toList());

        if (duplicatedFunctions.size() != 0) {
            throw new FunctionTableException(String.format("The function with the name %s and the params %s is already defined in module `%s`! Functions have to be unique in the combination of name and params (count and name)", funcName, params, module));
        }

        // adding to list of module
        functionsOfModule.add(function);
        // add update
        this.functions.put(module, functionsOfModule);
    }

    public FunctionNode get(String module, String functionName, int numberOfParams) throws FunctionTableException {
        assert module != null : "Module name is should never be null!";
        assert functionName != null : "Function name is not allowed to be empty";

        if (!this.functions.containsKey(module)) {
            throw new FunctionTableException(String.format("The module `%s` does not exist!", module));
        }

        // we can assure that we only have one in this
        // stream since `add` check that before insertion
        Optional<FunctionNode> optionalFunction = this.functions.get(module).stream()
                .filter(item -> functionName.equals(item.getName()))
                .filter(item -> item.getParamNames().size() == numberOfParams)
                .findFirst();

        return optionalFunction
                .orElseThrow(() ->
                        new FunctionTableException(
                                String.format("Cannot find the function %s with %d parameters in module `%s`", functionName, numberOfParams, module))
                );
    }


}
