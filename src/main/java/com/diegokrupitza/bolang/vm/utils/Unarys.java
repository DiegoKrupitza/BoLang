package com.diegokrupitza.bolang.vm.utils;

import com.diegokrupitza.bolang.vm.VirtualMachineException;
import com.diegokrupitza.bolang.vm.types.*;

import java.util.stream.Collectors;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 12.07.21
 */
public class Unarys {

    /**
     * Performs the unary operation of negation on a element
     * <p>
     * Types and the result:
     * <ul>
     *     <li><b><code>Types.NUMBER</code>:</b> the value multiplied with -1. The type stays the same</li>
     *     <li><b><code>Types.INTEGER_NUMBER</code>:</b> the value multiplied with -1. The type stays the same</li>
     *     <li><b><code>Types.STRING</code>:</b> flipping the chars in the ascii table. Means the character "T" (Ascii 84) becomes "+" (Ascii 43). The type stays the same</li>
     *     <li><b><code>Types.ARRAY</code>:</b> Negates the children of the array. The type stays for the elements the same. And returned will be an array</li>
     *     <li><b><code>Types.BOOLEAN</code>:</b> Flips the boolean value. Means true becomes false and the otherway around</li>
     * </ul>
     *
     * @param toNegate the element to negate
     * @return the negated element
     * @throws VirtualMachineException in case something goes not as planned. Aka you manage to provide an invalid type... Good luck!
     */
    public static AbstractElementType<?> performNegation(AbstractElementType<?> toNegate) throws VirtualMachineException {
        assert toNegate != null;

        if (toNegate.getType() == Type.DOUBLE) {
            DoubleElement evaluatedInType = (DoubleElement) toNegate;

            // just multiply itself with -1 to flip the sign
            evaluatedInType.setValue(-1 * evaluatedInType.getValue());
            return evaluatedInType;

        } else if (toNegate.getType() == Type.INTEGER_NUMBER) {
            IntegerElement evaluatedInType = (IntegerElement) toNegate;

            // just multiply itself with -1 to flip the sign
            evaluatedInType.setValue(-1 * evaluatedInType.getValue());
            return evaluatedInType;

        } else if (toNegate.getType() == Type.STRING) {
            // negating a string means you flip all the chars in the ascii tabel
            // element in ascii table at index 0 becomes 127
            // element in ascii table at index 1 becomes 126
            // ...
            // element in ascii table at index 65 becomes 62
            // ...
            // element in ascii table at index 127 becomes 0

            StringElement evaluatedInType = (StringElement) toNegate;

            String negatedString = evaluatedInType.getValue().chars()
                    .mapToObj(c -> (char) c)
                    .map(c -> 127 - ((int) c))
                    .map(c -> (char) c.intValue())
                    .map(Object::toString)
                    .collect(Collectors.joining());

            evaluatedInType.setValue(negatedString);
            return evaluatedInType;

        } else if (toNegate.getType() == Type.ARRAY) {

            ArrayElement arrayElement = (ArrayElement) toNegate;

            ArrayElement returnElement = new ArrayElement();

            for (AbstractElementType<?> abstractElementType : arrayElement.getValue()) {
                AbstractElementType<?> arrElem = Unarys.performNegation(abstractElementType);
                returnElement.add(arrElem);
            }

            return returnElement;
        } else if (toNegate.getType() == Type.BOOLEAN) {

            BooleanElement booleanElement = (BooleanElement) toNegate;

            return new BooleanElement(!booleanElement.getValue());

        } else {
            throw new VirtualMachineException("Contact the Bo Language administrator. Please include the code you run to reach this error! Code: 3");
        }
    }
}
