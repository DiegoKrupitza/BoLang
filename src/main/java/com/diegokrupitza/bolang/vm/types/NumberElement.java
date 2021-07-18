package com.diegokrupitza.bolang.vm.types;

import lombok.NonNull;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
public class NumberElement extends AbstractElementType<Double> implements Comparable<Double> {

    public NumberElement(@NonNull Double value) {
        super(value, Type.NUMBER);
    }

    @Override
    public int compareTo(Double o) {
        return this.getValue().compareTo(o);
    }

    public int compareTo(Integer o) {
        return this.getValue().compareTo(o.doubleValue());
    }
}
