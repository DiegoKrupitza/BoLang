package com.diegokrupitza.bolang.vm.types;

/**
 * All possible types that the virtual machine can handle
 * Between array and string the array is more dominant. This means an operation with array and string results in a new array
 * <p>
 * Between number and integer is number dominant. This means an operation on a Number and Integer the result will be a new Number.
 *
 * @author Diego Krupitza
 * @version 1.0
 * @date 08.07.21
 */
public enum Type {
    DOUBLE("Double", Double.class),
    INTEGER_NUMBER("Integer", Integer.class),
    STRING("String", String.class),
    ARRAY("Array", ArrayElement.class),
    BOOLEAN("Boolean", BooleanElement.class),
    VOID("Void", VoidElement.class);

    private final String name;
    private final Class<?> valueClass;

    Type(String name, Class<?> valueClass) {
        this.name = name;
        this.valueClass = valueClass;
    }

    public String getName() {
        return name;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
