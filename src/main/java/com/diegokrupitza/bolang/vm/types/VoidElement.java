package com.diegokrupitza.bolang.vm.types;

import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 28.07.21
 */
public class VoidElement extends AbstractElementType<Void> {

    public static final VoidElement NO_VALUE = new VoidElement();

    @SneakyThrows
    private VoidElement() {
        super(null, Type.VOID);
    }

    @Override
    public void setValue(Void value) {
        throw new UnsupportedOperationException("`setValue` on a void is not allowed to be called!");
    }

    @Override
    public void setType(@NonNull Type type) {
        throw new UnsupportedOperationException("`setType` on a void is not allowed to be called!");
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
