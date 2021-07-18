package com.diegokrupitza.bolang.vm.types;

import lombok.NonNull;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 13.07.21
 */
public class BooleanElement extends AbstractElementType<Boolean> {

    public BooleanElement(@NonNull Boolean value) {
        super(value, Type.BOOLEAN);
    }

}
