package com.diegokrupitza.bolang.vm.utils;

import com.diegokrupitza.bolang.vm.VirtualMachineException;
import com.diegokrupitza.bolang.vm.types.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 12.07.21
 */
public class Infixes {

    public static AbstractElementType<?> performAddition(AbstractElementType<?> leftElemInfo, AbstractElementType<?> rightElemInfo) throws VirtualMachineException {
        // both should never be null
        assert leftElemInfo != null;
        assert rightElemInfo != null;

        if (
                Types.atLeastOneNotOfTypes(java.util.Arrays.asList(Type.NUMBER, Type.INTEGER_NUMBER, Type.ARRAY), leftElemInfo, rightElemInfo)
        ) {
            throw new VirtualMachineException(String.format("You cannot perform an addition on %s and %s!", leftElemInfo.getType(), rightElemInfo.getType()));
        } else if (Types.atLeastOneOfType(Collections.singletonList(Type.ARRAY), leftElemInfo, rightElemInfo)) {
            // we have at least one array so this means we do the operation on each element
            List<AbstractElementType<?>> allArrays = Types.getOfTypes(Collections.singletonList(Type.ARRAY), leftElemInfo, rightElemInfo);

            if (allArrays.size() == 2) {
                // both are arrays means element by element
                ArrayElement leftArray = (ArrayElement) allArrays.get(0);
                ArrayElement rightArray = (ArrayElement) allArrays.get(1);


                ArrayElement workArray = (leftArray.size() >= rightArray.size()) ? rightArray : leftArray;
                ArrayElement biggerArray = (leftArray.size() >= rightArray.size()) ? leftArray : rightArray;


                ArrayElement returnArray = Arrays.emptyArray();

                int i = 0;
                for (; i < workArray.getValue().size(); i++) {
                    AbstractElementType<?> workElem = workArray.get(i);
                    AbstractElementType<?> biggerElem = biggerArray.get(i);

                    AbstractElementType<?> calced = null;
                    if (workArray.equals(leftArray)) {
                        calced = performAddition(workElem, biggerElem);
                    } else {
                        calced = performAddition(biggerElem, workElem);
                    }
                    returnArray.add(calced);
                }

                // adding the elements that not processed since the other array is to small
                returnArray.addAll(biggerArray.getFromIndex(i));

                return returnArray;
            } else {
                // only one is an array
                ArrayElement array = (ArrayElement) allArrays.get(0);
                AbstractElementType<?> otherNonArray = leftElemInfo;

                if (array.equals(leftElemInfo)) {
                    otherNonArray = rightElemInfo;
                }

                ArrayElement returnArray = Arrays.emptyArray();
                for (AbstractElementType<?> arrayElem : array.getValue()) {
                    if (array.equals(leftElemInfo)) {
                        // array is on the left side means array + elem
                        returnArray.add(performAddition(arrayElem, otherNonArray));
                    } else {
                        // array is on the right side means elem + array
                        returnArray.add(performAddition(otherNonArray, arrayElem));
                    }
                }
                return returnArray;
            }
        } else if (rightElemInfo.getType() == Type.INTEGER_NUMBER) {
            // the final type depends only on the left part since the right part is an integer
            // and Number dominates Integer_Number

            if (leftElemInfo.getType() == Type.INTEGER_NUMBER) {
                // result will be an integer
                Integer newValue = (Integer.parseInt(leftElemInfo.toString()) + Integer.parseInt(rightElemInfo.toString()));
                return new IntegerElement(newValue);
            } else {
                // result will be a number since the left part is a number
                Double newValue = Double.parseDouble(leftElemInfo.toString()) + Double.parseDouble(rightElemInfo.toString());
                return new NumberElement(newValue);
            }
        } else if (rightElemInfo.getType() == Type.NUMBER) {
            // the final type depends only on the right part since the right part is a number
            // and Number dominates anything
            Double newValue = Double.parseDouble(leftElemInfo.toString()) + Double.parseDouble(rightElemInfo.toString());
            return new NumberElement(newValue);
        }
        throw new VirtualMachineException("Should not happen!");
    }

    public static AbstractElementType<?> performDivision(AbstractElementType<?> leftElemInfo, AbstractElementType<?> rightElemInfo) throws VirtualMachineException {
        // both should never be null
        assert leftElemInfo != null;
        assert rightElemInfo != null;

        if (
                Types.atLeastOneNotOfTypes(java.util.Arrays.asList(Type.NUMBER, Type.INTEGER_NUMBER, Type.ARRAY), leftElemInfo, rightElemInfo)
        ) {
            throw new VirtualMachineException(String.format("You cannot perform a division on %s and %s!", leftElemInfo.getType(), rightElemInfo.getType()));
        } else if (Types.atLeastOneOfType(Collections.singletonList(Type.ARRAY), leftElemInfo, rightElemInfo)) {
            // we have at least one array so this means we do the operation on each element
            List<AbstractElementType<?>> allArrays = Types.getOfTypes(Collections.singletonList(Type.ARRAY), leftElemInfo, rightElemInfo);

            if (allArrays.size() == 2) {
                // both are arrays means element by element
                ArrayElement leftArray = (ArrayElement) allArrays.get(0);
                ArrayElement rightArray = (ArrayElement) allArrays.get(1);


                ArrayElement workArray = (leftArray.size() >= rightArray.size()) ? rightArray : leftArray;
                ArrayElement biggerArray = (leftArray.size() >= rightArray.size()) ? leftArray : rightArray;

                boolean leftIsAnArray = workArray.equals(leftArray);

                ArrayElement returnArray = Arrays.emptyArray();

                int i = 0;
                for (; i < workArray.getValue().size(); i++) {
                    AbstractElementType<?> workElem = workArray.get(i);
                    AbstractElementType<?> biggerElem = biggerArray.get(i);

                    AbstractElementType<?> calced;
                    if (leftIsAnArray) {
                        calced = performDivision(workElem, biggerElem);
                    } else {
                        calced = performDivision(biggerElem, workElem);
                    }
                    returnArray.add(calced);
                }

                // adding the elements that not processed since the other array is to small
                List<AbstractElementType<?>> rest = (!leftIsAnArray) ? biggerArray.getFromIndex(i) : biggerArray.getFromIndex(i)
                        .stream()
                        .map(item -> {
                            try {
                                if (item.getType() == Type.NUMBER || item.getType() == Type.INTEGER_NUMBER) {
                                    return performDivision(new NumberElement(0.0), item);
                                } else if (item.getType() == Type.ARRAY) {
                                    return performDivision(Arrays.emptyArray(), item);
                                } else {
                                    // will throw error
                                    return performDivision(new StringElement(""), item);
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e.getMessage());
                            }
                        })
                        .collect(Collectors.toList());
                returnArray.addAll(rest);

                return returnArray;
            } else {
                // only one is an array
                ArrayElement array = (ArrayElement) allArrays.get(0);
                AbstractElementType<?> otherNonArray = leftElemInfo;

                if (array.equals(leftElemInfo)) {
                    otherNonArray = rightElemInfo;
                }

                ArrayElement returnArray = Arrays.emptyArray();
                for (AbstractElementType<?> arrayElem : array.getValue()) {
                    if (array.equals(leftElemInfo)) {
                        // array is on the left side means array + elem
                        returnArray.add(performDivision(arrayElem, otherNonArray));
                    } else {
                        // array is on the right side means elem + array
                        returnArray.add(performDivision(otherNonArray, arrayElem));
                    }
                }
                return returnArray;
            }
        } else if (rightElemInfo.getType() == Type.INTEGER_NUMBER) {
            // the final type depends only on the left part since the right part is an integer
            // and Number dominates Integer_Number

            if (leftElemInfo.getType() == Type.INTEGER_NUMBER) {
                // result will be an integer
                if (Infixes.mod(leftElemInfo, rightElemInfo).getValue() == 0) {
                    // division works without converting to number
                    Integer newValue = (Integer.parseInt(leftElemInfo.toString()) / Integer.parseInt(rightElemInfo.toString()));
                    return new IntegerElement(newValue);
                }

                // division will return into a floating point number
                Double newValue = (Double.parseDouble(leftElemInfo.toString()) / Double.parseDouble(rightElemInfo.toString()));
                return new NumberElement(newValue);

            } else {
                // result will be a number since the left part is a number
                Double newValue = Double.parseDouble(leftElemInfo.toString()) / Double.parseDouble(rightElemInfo.toString());
                return new NumberElement(newValue);
            }
        } else if (rightElemInfo.getType() == Type.NUMBER) {
            // the final type depends only on the right part since the right part is a number
            // and Number dominates anything
            Double newValue = Double.parseDouble(leftElemInfo.toString()) / Double.parseDouble(rightElemInfo.toString());
            return new NumberElement(newValue);
        }
        throw new VirtualMachineException("Should not happen!");
    }

    public static IntegerElement mod(AbstractElementType<?> leftElemInfo, AbstractElementType<?> rightElemInfo) throws VirtualMachineException {
        if (
                Types.atLeastOneNotOfTypes(Collections.singletonList(Type.INTEGER_NUMBER), leftElemInfo, rightElemInfo)
        ) {
            throw new VirtualMachineException(String.format("You cannot perform a modul operation on %s and %s!", leftElemInfo.getType(), rightElemInfo.getType()));
        }
        IntegerElement leftCasted = (IntegerElement) leftElemInfo;
        IntegerElement rightCasted = (IntegerElement) rightElemInfo;

        return new IntegerElement(leftCasted.getValue() % rightCasted.getValue());
    }

    public static AbstractElementType<?> performMultiplication(AbstractElementType<?> leftElemInfo, AbstractElementType<?> rightElemInfo) throws VirtualMachineException {
        // both should never be null
        assert leftElemInfo != null;
        assert rightElemInfo != null;

        if (
                Types.atLeastOneNotOfTypes(java.util.Arrays.asList(Type.NUMBER, Type.INTEGER_NUMBER, Type.ARRAY), leftElemInfo, rightElemInfo)
        ) {
            throw new VirtualMachineException(String.format("You cannot perform a multiplication on %s and %s!", leftElemInfo.getType(), rightElemInfo.getType()));
        } else if (Types.atLeastOneOfType(Collections.singletonList(Type.ARRAY), leftElemInfo, rightElemInfo)) {
            // we have at least one array so this means we do the operation on each element
            List<AbstractElementType<?>> allArrays = Types.getOfTypes(Collections.singletonList(Type.ARRAY), leftElemInfo, rightElemInfo);

            if (allArrays.size() == 2) {
                // both are arrays means element by element
                ArrayElement leftArray = (ArrayElement) allArrays.get(0);
                ArrayElement rightArray = (ArrayElement) allArrays.get(1);


                ArrayElement workArray = (leftArray.size() >= rightArray.size()) ? rightArray : leftArray;
                ArrayElement biggerArray = (leftArray.size() >= rightArray.size()) ? leftArray : rightArray;


                ArrayElement returnArray = Arrays.emptyArray();

                int i = 0;
                for (; i < workArray.getValue().size(); i++) {
                    AbstractElementType<?> workElem = workArray.get(i);
                    AbstractElementType<?> biggerElem = biggerArray.get(i);

                    AbstractElementType<?> calced = null;
                    if (workArray.equals(leftArray)) {
                        calced = performMultiplication(workElem, biggerElem);
                    } else {
                        calced = performMultiplication(biggerElem, workElem);
                    }
                    returnArray.add(calced);
                }

                // adding the elements that not processed since the other array is to small
                returnArray.addAll(biggerArray.getFromIndex(i));

                return returnArray;
            } else {
                // only one is an array
                ArrayElement array = (ArrayElement) allArrays.get(0);
                AbstractElementType<?> otherNonArray = leftElemInfo;

                if (array.equals(leftElemInfo)) {
                    otherNonArray = rightElemInfo;
                }

                ArrayElement returnArray = Arrays.emptyArray();
                for (AbstractElementType<?> arrayElem : array.getValue()) {
                    if (array.equals(leftElemInfo)) {
                        // array is on the left side means array + elem
                        returnArray.add(performMultiplication(arrayElem, otherNonArray));
                    } else {
                        // array is on the right side means elem + array
                        returnArray.add(performMultiplication(otherNonArray, arrayElem));
                    }
                }
                return returnArray;
            }
        } else if (rightElemInfo.getType() == Type.INTEGER_NUMBER) {
            // the final type depends only on the left part since the right part is an integer
            // and Number dominates Integer_Number

            if (leftElemInfo.getType() == Type.INTEGER_NUMBER) {
                // result will be an integer
                Integer newValue = (Integer.parseInt(leftElemInfo.toString()) * Integer.parseInt(rightElemInfo.toString()));
                return new IntegerElement(newValue);
            } else {
                // result will be a number since the left part is a number
                Double newValue = Double.parseDouble(leftElemInfo.toString()) * Double.parseDouble(rightElemInfo.toString());
                return new NumberElement(newValue);
            }
        } else if (rightElemInfo.getType() == Type.NUMBER) {
            // the final type depends only on the right part since the right part is a number
            // and Number dominates anything
            Double newValue = Double.parseDouble(leftElemInfo.toString()) * Double.parseDouble(rightElemInfo.toString());
            return new NumberElement(newValue);
        }
        throw new VirtualMachineException("Should not happen!");
    }

    public static AbstractElementType<?> performSubtraction(AbstractElementType<?> leftElemInfo, AbstractElementType<?> rightElemInfo) throws VirtualMachineException {
        // both should never be null
        assert leftElemInfo != null;
        assert rightElemInfo != null;

        if (
                Types.atLeastOneNotOfTypes(java.util.Arrays.asList(Type.NUMBER, Type.INTEGER_NUMBER, Type.ARRAY), leftElemInfo, rightElemInfo)
        ) {
            throw new VirtualMachineException(String.format("You cannot perform a substraction on %s and %s!", leftElemInfo.getType(), rightElemInfo.getType()));
        } else if (Types.atLeastOneOfType(Collections.singletonList(Type.ARRAY), leftElemInfo, rightElemInfo)) {
            // we have at least one array so this means we do the operation on each element
            List<AbstractElementType<?>> allArrays = Types.getOfTypes(Collections.singletonList(Type.ARRAY), leftElemInfo, rightElemInfo);

            if (allArrays.size() == 2) {
                // both are arrays means element by element
                ArrayElement leftArray = (ArrayElement) allArrays.get(0);
                ArrayElement rightArray = (ArrayElement) allArrays.get(1);


                ArrayElement workArray = (leftArray.size() >= rightArray.size()) ? rightArray : leftArray;
                ArrayElement biggerArray = (leftArray.size() >= rightArray.size()) ? leftArray : rightArray;

                boolean leftIsAnArray = workArray.equals(leftArray);

                ArrayElement returnArray = Arrays.emptyArray();

                int i = 0;
                for (; i < workArray.getValue().size(); i++) {
                    AbstractElementType<?> workElem = workArray.get(i);
                    AbstractElementType<?> biggerElem = biggerArray.get(i);

                    AbstractElementType<?> calced = null;
                    if (leftIsAnArray) {
                        calced = performSubtraction(workElem, biggerElem);
                    } else {
                        calced = performSubtraction(biggerElem, workElem);
                    }
                    returnArray.add(calced);
                }

                // adding the elements that not processed since the other array is to small
                List<AbstractElementType<?>> rest = (!leftIsAnArray) ? biggerArray.getFromIndex(i) : biggerArray.getFromIndex(i)
                        .stream()
                        .map(item -> {
                            try {
                                if (item.getType() == Type.NUMBER) {
                                    return performSubtraction(new NumberElement(0.0), item);
                                } else if (item.getType() == Type.INTEGER_NUMBER) {
                                    return performSubtraction(new IntegerElement(0), item);
                                } else if (item.getType() == Type.ARRAY) {
                                    return performSubtraction(Arrays.emptyArray(), item);
                                } else {
                                    // will throw error
                                    return performSubtraction(new StringElement(""), item);
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e.getMessage());
                            }
                        })
                        .collect(Collectors.toList());
                returnArray.addAll(rest);

                return returnArray;
            } else {
                // only one is an array
                ArrayElement array = (ArrayElement) allArrays.get(0);
                AbstractElementType<?> otherNonArray = leftElemInfo;

                if (array.equals(leftElemInfo)) {
                    otherNonArray = rightElemInfo;
                }

                ArrayElement returnArray = Arrays.emptyArray();
                for (AbstractElementType<?> arrayElem : array.getValue()) {
                    if (array.equals(leftElemInfo)) {
                        // array is on the left side means array + elem
                        returnArray.add(performSubtraction(arrayElem, otherNonArray));
                    } else {
                        // array is on the right side means elem + array
                        returnArray.add(performSubtraction(otherNonArray, arrayElem));
                    }
                }
                return returnArray;
            }
        } else if (rightElemInfo.getType() == Type.INTEGER_NUMBER) {
            // the final type depends only on the left part since the right part is an integer
            // and Number dominates Integer_Number

            if (leftElemInfo.getType() == Type.INTEGER_NUMBER) {
                // result will be an integer
                Integer newValue = (Integer.parseInt(leftElemInfo.toString()) - Integer.parseInt(rightElemInfo.toString()));
                return new IntegerElement(newValue);
            } else {
                // result will be a number since the left part is a number
                Double newValue = Double.parseDouble(leftElemInfo.toString()) - Double.parseDouble(rightElemInfo.toString());
                return new NumberElement(newValue);
            }
        } else if (rightElemInfo.getType() == Type.NUMBER) {
            // the final type depends only on the right part since the right part is a number
            // and Number dominates anything
            Double newValue = Double.parseDouble(leftElemInfo.toString()) - Double.parseDouble(rightElemInfo.toString());
            return new NumberElement(newValue);
        }
        throw new VirtualMachineException("Should not happen!");
    }

    public static AbstractElementType<?> performStringConcatenation(AbstractElementType<?> leftElemInfo, AbstractElementType<?> rightElemInfo) {
        // first element gets converted to string (if already string nothing happens) the same with the right element
        // then concatinated
        String newVal = leftElemInfo.getValue() + "" + rightElemInfo.getValue();
        return new StringElement(newVal);
    }
}
