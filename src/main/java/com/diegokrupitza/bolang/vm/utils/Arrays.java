package com.diegokrupitza.bolang.vm.utils;

import com.diegokrupitza.bolang.vm.types.AbstractElementType;
import com.diegokrupitza.bolang.vm.types.ArrayElement;
import com.diegokrupitza.bolang.vm.types.Type;
import org.apache.commons.collections4.CollectionUtils;

import java.util.stream.Collectors;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
public class Arrays {

    public static ArrayElement emptyArray() {
        return new ArrayElement();
    }

    public static boolean isEmpty(ArrayElement array) {
        if (!isArray(array)) {
            throw new IllegalArgumentException("You wanted to call this method with an element not of type array!");
        }

        return CollectionUtils.isEmpty(array.getValue());
    }

    public static boolean isArray(AbstractElementType<?> array) {
        return array.getType() == Type.ARRAY && array instanceof ArrayElement;
    }

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
