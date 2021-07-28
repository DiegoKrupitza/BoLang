package com.diegokrupitza.bolang.vm.functions.impl;

import com.diegokrupitza.bolang.vm.functions.BoFunction;
import com.diegokrupitza.bolang.vm.functions.Function;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionException;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionParameterException;
import com.diegokrupitza.bolang.vm.types.AbstractElementType;
import com.diegokrupitza.bolang.vm.types.DoubleElement;
import com.diegokrupitza.bolang.vm.types.IntegerElement;
import com.diegokrupitza.bolang.vm.types.Type;
import com.diegokrupitza.bolang.vm.utils.Types;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 10.07.21
 */
@BoFunction(name = "rand", description = "Random number between the specified lower bound (inclusive) and upper bound (exclusive). By default lower bound is 0 and upper bound 1")
@Getter
public class RandFunction implements Function {

    private Number upperBound = 1;
    private Number lowerBound = 0;

    @Override
    public void paramCheck(List<AbstractElementType<?>> params) throws BoFunctionException {
        // date function takes one param or none
        // if one param is given it has to be a string
        if (CollectionUtils.isEmpty(params)) {
            return;
        }

        if (Types.atLeastOneNotOfTypes(List.of(Type.INTEGER_NUMBER, Type.DOUBLE), params.toArray(new AbstractElementType<?>[0]))) {
            String wrongParamsTypes = params.stream()
                    .map(i -> i.getType().getName())
                    .collect(Collectors.joining(","));
            throw new BoFunctionParameterException(String.format("The `rand` function only allows numbers and integers to be parameters. You provided %s!", wrongParamsTypes));
        }

        if (params.size() == 1) {
            // only upper bound
            upperBound = (Number) params.get(0).getValue();
        } else if (params.size() == 2) {
            // lower and upper bound
            lowerBound = (Number) params.get(0).getValue();
            upperBound = (Number) params.get(1).getValue();
        } else {
            // this should not happen...
            throw new BoFunctionParameterException(String.format("The `rand` function only takes 0,1 or 2 parameters! You served %s", params.size()));
        }

        if (lowerBound.doubleValue() > upperBound.doubleValue()) {
            throw new BoFunctionParameterException("When calling the `rand` function the upper bound has to be greater or equals to the lower bound!");
        }
    }

    @Override
    public AbstractElementType<?> execute(List<AbstractElementType<?>> params) throws BoFunctionException {
        double randValue = ThreadLocalRandom.current().nextDouble(lowerBound.doubleValue(), upperBound.doubleValue());
        return new DoubleElement(randValue);
    }
}
