package com.diegokrupitza.bolang.vm.functions.exceptions;

import lombok.NoArgsConstructor;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 10.07.21
 */
@NoArgsConstructor
public class BoFunctionNotFoundException extends BoFunctionException {
    public BoFunctionNotFoundException(String message) {
        super(message);
    }
}
