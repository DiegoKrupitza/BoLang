package com.diegokrupitza.bolang.vm.functions.impl.random;

import com.diegokrupitza.bolang.vm.functions.BoFunction;
import com.diegokrupitza.bolang.vm.functions.Function;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionException;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionParameterException;
import com.diegokrupitza.bolang.vm.types.AbstractElementType;
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
@BoFunction(name = "randInt", module = "Random", description = "Random integer between the specified lower bound (inclusive) and upper bound (exclusive). By default lower bound is 0 and upper bound 1")
@Getter
public class RandIntFunction implements Function {

    private Number upperBound = 1;
    private Number lowerBound = 0;
    private boolean noParams = false;

    @Override
    public void paramCheck(List<AbstractElementType<?>> params) throws BoFunctionException {
        // date function takes one param or none
        // if one param is given it has to be a string
        if (CollectionUtils.isEmpty(params)) {
            noParams = true;
            return;
        }

        if (Types.atLeastOneNotOfTypes(List.of(Type.INTEGER_NUMBER, Type.DOUBLE), params.toArray(new AbstractElementType<?>[0]))) {
            String wrongParamsTypes = params.stream()
                    .map(i -> i.getType().getName())
                    .collect(Collectors.joining(","));
            throw new BoFunctionParameterException(String.format("The `randInt` function only allows numbers and integers to be parameters. You provided %s!", wrongParamsTypes));
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
            throw new BoFunctionParameterException(String.format("The `randInt` function only takes 0,1 or 2 parameters! You served %s", params.size()));
        }

        if (lowerBound.intValue() > upperBound.intValue()) {
            throw new BoFunctionParameterException("When calling the `randInt` function the upper bound has to be greater or equals to the lower bound!");
        }
    }

    @Override
    public AbstractElementType<?> execute(List<AbstractElementType<?>> params) throws BoFunctionException {
        int randValue;
        if (noParams) {
            randValue = ThreadLocalRandom.current().nextInt();
        } else {
            randValue = ThreadLocalRandom.current().nextInt(lowerBound.intValue(), upperBound.intValue());
        }
        return new IntegerElement(randValue);
    }
}
