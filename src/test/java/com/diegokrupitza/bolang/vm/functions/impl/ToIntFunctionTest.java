package com.diegokrupitza.bolang.vm.functions.impl;

import com.diegokrupitza.bolang.vm.functions.Function;
import com.diegokrupitza.bolang.vm.functions.FunctionFactory;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionCallException;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionParameterException;
import com.diegokrupitza.bolang.vm.types.*;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
public class ToIntFunctionTest {

    private Function toInt;

    @BeforeEach
    public void init() {
        toInt = FunctionFactory.getFunction("toInt");
    }

    @Test
    public void checkParamsValidTest() {
        DoubleElement doubleElement = new DoubleElement(12.12);

        toInt.paramCheck(Collections.singletonList(doubleElement));
    }

    @Test
    public void checkParamsInvalidTest() {
        assertThatThrownBy(() -> toInt.paramCheck(new ArrayList<>()))
                .isInstanceOf(BoFunctionParameterException.class)
                .hasMessage("The function `toInt`requires exactly one parameter. The parameter can be of any type");
    }

    @Test
    public void checkParamsInvalidTooManyTest() {
        DoubleElement doubleElement1 = new DoubleElement(12.12);
        DoubleElement doubleElement2 = new DoubleElement(99.12);

        assertThatThrownBy(() -> toInt.paramCheck(Arrays.asList(doubleElement1, doubleElement2)))
                .isInstanceOf(BoFunctionParameterException.class)
                .hasMessage("The function `toInt`requires exactly one parameter. The parameter can be of any type");
    }


    @Test
    public void toIntNumberTest() {
        DoubleElement doubleElement = new DoubleElement(12.12);
        AbstractElementType<?> call = toInt.call(Collections.singletonList(doubleElement));

        assertThat(call).isInstanceOf(IntegerElement.class);
        assertThat(call.getType()).isEqualTo(Type.INTEGER_NUMBER);
        assertThat(((IntegerElement) call).getValue()).isEqualTo(12);
    }

    @Test
    public void toIntIntegerTest() {
        IntegerElement integerElement = new IntegerElement(100);
        AbstractElementType<?> call = toInt.call(Collections.singletonList(integerElement));

        assertThat(call).isInstanceOf(IntegerElement.class);
        assertThat(call.getType()).isEqualTo(Type.INTEGER_NUMBER);
        assertThat(((IntegerElement) call).getValue()).isEqualTo(100);
    }

    @Test
    public void toIntStringTest() {
        StringElement stringElement = new StringElement("100");
        AbstractElementType<?> call = toInt.call(Collections.singletonList(stringElement));

        assertThat(call).isInstanceOf(IntegerElement.class);
        assertThat(call.getType()).isEqualTo(Type.INTEGER_NUMBER);
        assertThat(((IntegerElement) call).getValue()).isEqualTo(100);
    }

    @Test
    public void toIntArrayTest() {
        StringElement stringElement = new StringElement("100");
        IntegerElement integerElement = new IntegerElement(100);
        DoubleElement doubleElement = new DoubleElement(12.12);

        ArrayElement arrayElement = new ArrayElement(Arrays.asList(stringElement, integerElement, doubleElement));

        AbstractElementType<?> call = toInt.call(Collections.singletonList(arrayElement));

        assertThat(call).isInstanceOf(ArrayElement.class);
        assertThat(call.getType()).isEqualTo(Type.ARRAY);

        @NonNull List<AbstractElementType<?>> value = ((ArrayElement) call).getValue();
        assertThat(value)
                .isNotEmpty()
                .hasSize(3);

        assertThat(((IntegerElement) value.get(0)).getValue()).isEqualTo(100);
        assertThat(((IntegerElement) value.get(1)).getValue()).isEqualTo(100);
        assertThat(((IntegerElement) value.get(2)).getValue()).isEqualTo(12);
    }

    @Test
    public void toIntStringInvalidTest() {
        StringElement stringElement = new StringElement("Invalid");
        assertThatThrownBy(() -> toInt.call(Collections.singletonList(stringElement)))
                .isInstanceOf(BoFunctionCallException.class)
                .hasMessage(String.format("Can not convert `%s` into an integer!", stringElement.getValue()));
    }

    @Test
    public void toIntBooleanTrueTest() {
        BooleanElement booleanElement = new BooleanElement(true);
        AbstractElementType<?> call = toInt.call(Collections.singletonList(booleanElement));

        assertThat(call).isInstanceOf(IntegerElement.class);
        assertThat(call.getType()).isEqualTo(Type.INTEGER_NUMBER);
        assertThat(((IntegerElement) call).getValue()).isEqualTo(1);
    }

    @Test
    public void toIntBooleanFalseTest() {
        BooleanElement booleanElement = new BooleanElement(false);
        AbstractElementType<?> call = toInt.call(Collections.singletonList(booleanElement));

        assertThat(call).isInstanceOf(IntegerElement.class);
        assertThat(call.getType()).isEqualTo(Type.INTEGER_NUMBER);
        assertThat(((IntegerElement) call).getValue()).isEqualTo(0);
    }

}
