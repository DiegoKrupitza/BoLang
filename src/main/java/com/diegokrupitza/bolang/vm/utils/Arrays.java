package com.diegokrupitza.bolang.vm.utils;

import com.diegokrupitza.bolang.vm.types.AbstractElementType;
import com.diegokrupitza.bolang.vm.types.ArrayElement;
import com.diegokrupitza.bolang.vm.types.Type;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
public class Arrays {

    private Arrays() {
        // this class should only be accessed by static methods
    }

    /**
     * Generates a new empty array.
     *
     * @return an empty array
     */
    public static ArrayElement emptyArray() {
        return new ArrayElement();
    }

    /**
     * Creates an array with the provided elements
     *
     * @param elements the elements we want to be inside the array
     * @return the newly created array with all the elements in it
     */
    public static ArrayElement of(AbstractElementType<?>... elements) {
        return new ArrayElement(List.of(elements));
    }

    /**
     * Checks if an array is empty or not
     *
     * @param array the array to check on
     * @return <code>true</code> when the array is empty otherwise <code>false</code>
     */
    public static boolean isEmpty(ArrayElement array) {
        return CollectionUtils.isEmpty(array.getValue());
    }

    /**
     * Checks if the provided element is an array
     *
     * @param element the element to check on
     * @return <code>true</code> when it is an array otherwise <code>false</code>
     */
    public static boolean isArray(AbstractElementType<?> element) {
        return Objects.nonNull(element) && element.getType() == Type.ARRAY && element instanceof ArrayElement;
    }

    /**
     * Serves the string representation of the array.
     * An array is display in the following form:
     * <code>[element1, element2, ...]</code>
     *
     * @param array the array of which to get its string representation
     * @return the string representation of the given array
     */
    public static String toString(ArrayElement array) {

        String valuesAsString = array.getValue().stream()
                .map(item -> {
                    if (item instanceof ArrayElement) {
                        return Arrays.toString((ArrayElement) item);
                    } else {
                        return item.toString();
                    }
                })
                .collect(Collectors.joining(", "));

        return "[" + valuesAsString + "]";
    }

}
