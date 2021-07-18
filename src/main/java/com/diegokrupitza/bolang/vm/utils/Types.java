package com.diegokrupitza.bolang.vm.utils;

import com.diegokrupitza.bolang.vm.types.AbstractElementType;
import com.diegokrupitza.bolang.vm.types.Type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 13.07.21
 */
public class Types {

    /**
     * Gets all the elements which type is in the presented types list.
     *
     * @param types    the types of elements we want to filter out
     * @param elements the elements we want to perform the filter on
     * @return all the elements that are one of the types given in the types list
     */
    public static List<AbstractElementType<?>> getOfTypes(List<Type> types, AbstractElementType<?>... elements) {
        return java.util.Arrays.stream(elements)
                .filter(item -> types.contains(item.getType()))
                .collect(Collectors.toList());
    }

    /**
     * Checks if all the elements are one of the given types
     *
     * @param types    the list of types we want to check on
     * @param elements the elements to check
     * @return <code>true</code> when all elements are at least of one type in the list otherwise <code>false</code>
     */
    public static boolean allOfTypes(List<Type> types, AbstractElementType<?>... elements) {
        return java.util.Arrays.stream(elements)
                .filter(item -> types.contains(item.getType()))
                .count() == elements.length;
    }

    /**
     * Checks if at least one element is not in the types list
     *
     * @param types    the types to check on
     * @param elements the elements we want to check on
     * @return <code>true</code> when at least on elements type is not in the given types list otherwise <code>false</code>
     */
    public static boolean atLeastOneNotOfTypes(List<Type> types, AbstractElementType<?>... elements) {
        return !allOfTypes(types, elements);
    }

    /**
     * Checks if at least ones element type is present in the types list
     *
     * @param types    the types we want check on
     * @param elements the elements we want to check
     * @return <code>true</code> when at least on elements type is in the given types list otherwise <code>false</code>
     */
    public static boolean atLeastOneOfType(List<Type> types, AbstractElementType<?>... elements) {
        return Arrays.stream(elements)
                .filter(item -> types.contains(item.getType()))
                .count() >= 1;
    }

    /**
     * Checks if both elements are of the same given type
     *
     * @param type the type both elements should be
     * @param e1   the first element
     * @param e2   the second element
     * @return <code>true</code> when both elements are the same type otherwise <code>false</code>
     */
    public static boolean isBothOfType(Type type, AbstractElementType<?> e1, AbstractElementType<?> e2) {
        return e1.getType() == type && e2.getType() == type;
    }

}
