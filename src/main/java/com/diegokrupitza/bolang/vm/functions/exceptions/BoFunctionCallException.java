package com.diegokrupitza.bolang.vm.functions.exceptions;

import lombok.NoArgsConstructor;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
@NoArgsConstructor
public class BoFunctionCallException extends BoFunctionException {
    public BoFunctionCallException(String errorMessage) {
        super(errorMessage);
    }
}
