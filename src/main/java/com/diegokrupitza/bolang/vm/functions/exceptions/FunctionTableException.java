package com.diegokrupitza.bolang.vm.functions.exceptions;

import com.diegokrupitza.bolang.vm.VirtualMachineException;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 03.08.21
 */
public class FunctionTableException extends VirtualMachineException {
    public FunctionTableException(String s) {
        super(s);
    }
}
