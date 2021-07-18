package com.diegokrupitza.bolang.vm.functions;

import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionException;
import com.diegokrupitza.bolang.vm.types.AbstractElementType;

import java.util.List;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 10.07.21
 */
public interface Function {

    /**
     * Calls the given BoFunction with the params
     *
     * @param params the params to call the function
     * @return the value the function generated
     * @throws BoFunctionException in case something goes wrong!
     */
    default AbstractElementType<?> call(List<AbstractElementType<?>> params) throws BoFunctionException {
        paramCheck(params);
        return execute(params);
    }

    /**
     * Checks if the given params are what the function expects.
     * If the params do not fit the function an error message will be returned.
     *
     * @param params the params to check
     * @throws BoFunctionException in case there is an error with the params
     */
    void paramCheck(List<AbstractElementType<?>> params) throws BoFunctionException;

    /**
     * The real function code. Here you write what will happen
     *
     * @param params the params we work on
     * @return the value the function generated
     * @throws BoFunctionException in case something goes wrong!
     */
    AbstractElementType<?> execute(List<AbstractElementType<?>> params) throws BoFunctionException;

}
