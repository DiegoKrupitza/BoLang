package com.diegokrupitza.bolang.vm.types;

import lombok.NonNull;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
public class IntegerElement extends NumberElement<Integer> {

    public IntegerElement(@NonNull Integer value) {
        super(value, Type.INTEGER_NUMBER);
    }

    @Override
    public int compareTo(Integer o) {
        return this.getValue().compareTo(o);
    }

    public int compareTo(Double o) {
        return ((Double) this.getValue().doubleValue()).compareTo(o);
    }
}
