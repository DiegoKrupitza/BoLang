package com.diegokrupitza.bolang.vm.functions.impl;

import com.diegokrupitza.bolang.vm.functions.BoFunction;
import com.diegokrupitza.bolang.vm.functions.Function;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionCallException;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionException;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionParameterException;
import com.diegokrupitza.bolang.vm.types.AbstractElementType;
import com.diegokrupitza.bolang.vm.types.ArrayElement;
import com.diegokrupitza.bolang.vm.types.IntegerElement;
import com.diegokrupitza.bolang.vm.types.Type;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
@BoFunction(name = "toInt", description = "Converts the first argument from any format to an integer number")
public class ToIntFunction implements Function {

    @Override
    public void paramCheck(List<AbstractElementType<?>> params) throws BoFunctionException {
        if (CollectionUtils.isEmpty(params)) {
            throw new BoFunctionParameterException("The function `toInt`requires exactly one parameter. The parameter can be of any type");
        } else if (params.size() != 1) {
            throw new BoFunctionParameterException("The function `toInt`requires exactly one parameter. The parameter can be of any type");
        }
        // everything went fine
    }

    @Override
    public AbstractElementType<?> execute(List<AbstractElementType<?>> params) throws BoFunctionException {
        AbstractElementType<?> paramToCast = params.get(0);
        return performCastToInt(paramToCast);
    }

    private AbstractElementType<?> performCastToInt(AbstractElementType<?> paramToCast) throws BoFunctionCallException {
        @NonNull Type beforeType = paramToCast.getType();

        if (beforeType == Type.INTEGER_NUMBER) {
            // its already an int so we do not need to do anything...
            return paramToCast;
        } else if (beforeType == Type.DOUBLE) {
            // we simply cut of the decimal part

            int newVal = ((Double) paramToCast.getValue()).intValue();
            return new IntegerElement(newVal);
        } else if (beforeType == Type.ARRAY) {
            // we take each array entry and do the same recursive

            ArrayElement paramToCastOfType = (ArrayElement) paramToCast;

            // casting each element of the array with `toInt`:)
            List<AbstractElementType<?>> newVal = paramToCastOfType.getValue().stream()
                    .map(this::performCastToInt)
                    .collect(Collectors.toList());

            paramToCastOfType.setValue(newVal);

            return paramToCastOfType;

        } else if (beforeType == Type.STRING) {
            // toInt here has the meaning that the string contains a number so we just mark it as a number
            String beforeVal = (String) paramToCast.getValue();
            try {
                int newVal = Integer.parseInt(beforeVal);
                return new IntegerElement(newVal);
            } catch (NumberFormatException e) {
                // aaah you sneaky guy! you wanted to trick us by giving a string that cant be parsed ;)
                throw new BoFunctionCallException(String.format("Can not convert `%s` into an integer!", beforeVal));
            }
        } else if (beforeType == Type.BOOLEAN) {
            // true -> 1
            // false -> 0
            Boolean value = (Boolean) paramToCast.getValue();
            return new IntegerElement(value ? 1 : 0);
        }

        throw new BoFunctionCallException("This should never be reached! toInt function! Please contact the administrator! Include your code in the message! Thank you!");
    }
}
