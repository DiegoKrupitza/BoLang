package com.diegokrupitza.bolang.vm.types;

import lombok.NonNull;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
public class DoubleElement extends NumberElement<Double> {

    public DoubleElement(@NonNull Double value) {
        super(value, Type.DOUBLE);
    }

    @Override
    public int compareTo(Double o) {
        return this.getValue().compareTo(o);
    }

    public int compareTo(Integer o) {
        return this.getValue().compareTo(o.doubleValue());
    }
}
