package com.diegokrupitza.bolang.vm.functions;

import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionCreationException;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionException;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionNotFoundException;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 10.07.21
 */
public class FunctionFactory {

    // only scanning the Bolang package
    private static final Reflections reflections = new Reflections("com.diegokrupitza.bolang");

    // all functions that implement the interface "Function"
    private static final Set<Class<? extends Function>> allInterfaceFunctions = reflections.getSubTypesOf(Function.class);

    // all function that are annotated with the annotation "BoFunction"
    private static final Set<Class<?>> allAnnotatedFunction = reflections.getTypesAnnotatedWith(BoFunction.class);

    /**
     * Gets the function that can be identified with the given function name
     *
     * @param moduleName   the name of the module the function is located in
     * @param functionName the name of the function we want to receive
     * @return the function with the name we provided
     * @throws BoFunctionNotFoundException in case the function does not exist or there are multiple definitions
     */
    public static Function getFunction(String moduleName, String functionName) throws BoFunctionException {
        List<Class<? extends Function>> allFunctions = getAllPredefinedFunctions();


        // filter the function with matching name
        List<Class<? extends Function>> functionsWithThatName = allFunctions.stream()
                .filter(item -> item.isAnnotationPresent(BoFunction.class))
                .filter(item -> item.getAnnotation(BoFunction.class).module().equals(moduleName))
                .filter(item -> item.getAnnotation(BoFunction.class).name().equals(functionName))
                .collect(Collectors.toList());

        // too many function with the same name
        if (functionsWithThatName.size() > 1) {
            throw new BoFunctionNotFoundException(String.format("The function with the name %s in module %s has multiple definitions", functionName, moduleName));
        }

        // no function with that name
        if (functionsWithThatName.size() == 0) {
            throw new BoFunctionNotFoundException(String.format("The function with the name %s in module %s does not exist!", functionName, moduleName));
        }

        // exact one function with that name
        // get the class object that is that one
        Class<? extends Function> aClass = functionsWithThatName.stream().findFirst().get();

        try {
            // creating a new object of that function we want to call
            // important is that there is no params allowed for a function in its constructor
            return aClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new BoFunctionCreationException(e.getMessage());
        }
    }

    /**
     * Provides a Set of all the Module names in the BoLang programming language
     *
     * @return a Set of Module names
     */
    public static Set<String> getAllPredefinedModules() {
        return getAllPredefinedFunctions().stream()
                .map(item -> item.getAnnotation(BoFunction.class).module())
                .collect(Collectors.toUnmodifiableSet());
    }

    private static List<Class<? extends Function>> getAllPredefinedFunctions() {
        // all functions that implement the interface "Function" and the annotation "BoFunction"
        return allInterfaceFunctions.stream()
                .filter(allAnnotatedFunction::contains)
                .collect(Collectors.toList());
    }

}
