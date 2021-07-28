package com.diegokrupitza.bolang.vm.types;

import lombok.NonNull;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 28.07.21
 */
public abstract class NumberElement<T extends Number> extends AbstractElementType<T> implements Comparable<T> {

    protected NumberElement(@NonNull T value, @NonNull Type type) {
        super(value, type);
    }
}
