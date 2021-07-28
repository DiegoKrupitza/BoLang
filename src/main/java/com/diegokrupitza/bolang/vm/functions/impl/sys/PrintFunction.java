package com.diegokrupitza.bolang.vm.functions.impl.sys;

import com.diegokrupitza.bolang.vm.functions.BoFunction;
import com.diegokrupitza.bolang.vm.functions.Function;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionException;
import com.diegokrupitza.bolang.vm.types.AbstractElementType;
import com.diegokrupitza.bolang.vm.types.VoidElement;
import lombok.Getter;

import java.util.List;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 28.07.21
 */
@BoFunction(module = "Sys", name = "print")
@Getter
public class PrintFunction implements Function {
    @Override
    public void paramCheck(List<AbstractElementType<?>> params) throws BoFunctionException {
        //This is empty since we simply print out all the params
    }

    @Override
    public AbstractElementType<?> execute(List<AbstractElementType<?>> params) throws BoFunctionException {
        params.forEach(obj -> System.out.print(obj.getValue()));
        return VoidElement.NO_VALUE;
    }
}
