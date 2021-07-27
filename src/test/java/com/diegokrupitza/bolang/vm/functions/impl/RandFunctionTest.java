package com.diegokrupitza.bolang.vm.functions.impl;

import com.diegokrupitza.bolang.vm.functions.Function;
import com.diegokrupitza.bolang.vm.functions.FunctionFactory;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionParameterException;
import com.diegokrupitza.bolang.vm.types.*;
import com.diegokrupitza.bolang.vm.utils.Booleans;
import org.apache.commons.lang3.Range;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
public class RandFunctionTest {

    private Function rand;

    public static Stream<Arguments> checkParamsSingleParamSource() {
        return Stream.of(
                Arguments.of(new NumberElement(10.0), false),
                Arguments.of(new IntegerElement(10), false),
                Arguments.of(Booleans.FALSE, true),
                Arguments.of(Booleans.TRUE, true),
                Arguments.of(new StringElement(""), true),
                Arguments.of(com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), true)
        );
    }

    public static Stream<Arguments> randSource() {
        return Stream.of(
                Arguments.of(new NumberElement(10.0), null, Range.between(0.0, 10.0)),
                Arguments.of(new NumberElement(1.0), null, Range.between(0.0, 1.0)),
                Arguments.of(new IntegerElement(10), null, Range.between(0.0, 10.0)),
                Arguments.of(new IntegerElement(1), null, Range.between(0.0, 1.0)),

                Arguments.of(new NumberElement(10.0), new NumberElement(11.0), Range.between(0.0, 11.0)),
                Arguments.of(new NumberElement(1.0), new NumberElement(11.0), Range.between(0.0, 11.0)),
                Arguments.of(new IntegerElement(10), new NumberElement(11.0), Range.between(0.0, 11.0)),
                Arguments.of(new IntegerElement(1), new NumberElement(11.0), Range.between(0.0, 11.0)),

                Arguments.of(new NumberElement(10.0), new IntegerElement(11), Range.between(0.0, 11.0)),
                Arguments.of(new NumberElement(1.0), new IntegerElement(11), Range.between(0.0, 11.0)),
                Arguments.of(new IntegerElement(10), new IntegerElement(11), Range.between(0.0, 11.0)),
                Arguments.of(new IntegerElement(1), new IntegerElement(11), Range.between(0.0, 11.0))
        );
    }


    public static Stream<Arguments> checkParamsTwoParamSource() {
        return Stream.of(
                Arguments.of(new NumberElement(10.0), new NumberElement(11.0), false),
                Arguments.of(new IntegerElement(10), new NumberElement(11.0), false),
                Arguments.of(Booleans.FALSE, new NumberElement(11.0), true),
                Arguments.of(Booleans.TRUE, new NumberElement(11.0), true),
                Arguments.of(new StringElement(""), new NumberElement(11.0), true),
                Arguments.of(com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), new NumberElement(11.0), true),

                Arguments.of(new NumberElement(11.0), new NumberElement(210.0), false),
                Arguments.of(new NumberElement(11.0), new IntegerElement(210), false),
                Arguments.of(new NumberElement(11.0), Booleans.FALSE, true),
                Arguments.of(new NumberElement(11.0), Booleans.TRUE, true),
                Arguments.of(new NumberElement(11.0), new StringElement(""), true),
                Arguments.of(new NumberElement(11.0), com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), true),

                Arguments.of(new NumberElement(10.0), new IntegerElement(11), false),
                Arguments.of(new IntegerElement(10), new IntegerElement(11), false),
                Arguments.of(Booleans.FALSE, new IntegerElement(11), true),
                Arguments.of(Booleans.TRUE, new IntegerElement(11), true),
                Arguments.of(new StringElement(""), new IntegerElement(11), true),
                Arguments.of(com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), new IntegerElement(11), true),


                Arguments.of(new IntegerElement(11), new NumberElement(210.0), false),
                Arguments.of(new IntegerElement(11), new IntegerElement(210), false),
                Arguments.of(new IntegerElement(11), Booleans.FALSE, true),
                Arguments.of(new IntegerElement(11), Booleans.TRUE, true),
                Arguments.of(new IntegerElement(11), new StringElement(""), true),
                Arguments.of(new IntegerElement(11), com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), true)
        );
    }

    public static Stream<Arguments> checkParamsLowerAndUpperBoundSource() {
        return Stream.of(
                Arguments.of(new NumberElement(10.0), new NumberElement(11.0), false),
                Arguments.of(new IntegerElement(10), new NumberElement(11.0), false),
                Arguments.of(new NumberElement(210.0), new NumberElement(11.0), true),
                Arguments.of(new IntegerElement(21), new NumberElement(11.0), true),

                Arguments.of(new NumberElement(10.0), new IntegerElement(11), false),
                Arguments.of(new IntegerElement(10), new IntegerElement(11), false),
                Arguments.of(new NumberElement(210.0), new IntegerElement(11), true),
                Arguments.of(new IntegerElement(21), new IntegerElement(11), true)
        );
    }

    @BeforeEach
    public void init() {
        rand = FunctionFactory.getFunction("rand");
    }

    @ParameterizedTest
    @MethodSource("checkParamsSingleParamSource")
    public void checkParamsSingleParamTest(AbstractElementType<?> param, boolean shouldThrow) {
        List<AbstractElementType<?>> params = Collections.singletonList(param);

        if (shouldThrow) {
            assertThatThrownBy(() -> rand.paramCheck(params))
                    .isInstanceOf(BoFunctionParameterException.class)
                    .hasMessage(String.format("The `rand` function only allows numbers and integers to be parameters. You provided %s!", param.getType().getName()));
        } else {
            rand.paramCheck(params);
        }
    }

    @ParameterizedTest
    @MethodSource("checkParamsTwoParamSource")
    public void checkParamsTwoParamTest(AbstractElementType<?> e1, AbstractElementType<?> e2, boolean shouldThrow) {
        List<AbstractElementType<?>> params = List.of(e1, e2);

        if (shouldThrow) {
            assertThatThrownBy(() -> rand.paramCheck(params))
                    .isInstanceOf(BoFunctionParameterException.class)
                    .hasMessage(String.format("The `rand` function only allows numbers and integers to be parameters. You provided %s,%s!", e1.getType().getName(), e2.getType().getName()));
        } else {
            rand.paramCheck(params);
        }
    }

    @Test
    public void checkParamsInvalidTooManyTest() {
        NumberElement numberElement1 = new NumberElement(12.12);
        NumberElement numberElement2 = new NumberElement(99.12);
        NumberElement numberElement3 = new NumberElement(99.12);

        assertThatThrownBy(() -> rand.paramCheck(List.of(numberElement1, numberElement2, numberElement3)))
                .isInstanceOf(BoFunctionParameterException.class)
                .hasMessage(String.format("The `rand` function only takes 0,1 or 2 parameters! You served %s", 3));
    }

    @ParameterizedTest
    @MethodSource("checkParamsLowerAndUpperBoundSource")
    public void checkParamsLowerAndUpperBoundTest(AbstractElementType<?> e1, AbstractElementType<?> e2, boolean shouldThrow) {
        List<AbstractElementType<?>> params;
        if (e2 == null) {
            params = List.of(e1);
        } else {
            params = List.of(e1, e2);
        }

        if (shouldThrow) {
            assertThatThrownBy(() -> rand.paramCheck(params))
                    .isInstanceOf(BoFunctionParameterException.class)
                    .hasMessage("When calling the `rand` function the upper bound has to be greater or equals to the lower bound!");
        } else {
            rand.paramCheck(params);
        }
    }


    @ParameterizedTest
    @MethodSource("randSource")
    public void randTest(AbstractElementType<?> e1, AbstractElementType<?> e2, Range<Double> range) {
        List<AbstractElementType<?>> params;
        if (e2 == null) {
            params = List.of(e1);
        } else {
            params = List.of(e1, e2);
        }

        AbstractElementType<?> call = rand.call(params);

        assertThat(call).isInstanceOf(NumberElement.class);
        assertThat(call.getType()).isEqualTo(Type.NUMBER);
        assertThat(((NumberElement) call).getValue()).isBetween(range.getMinimum(), range.getMaximum());
    }

    @Test
    public void randEmptyTest() {
        AbstractElementType<?> call = rand.call(List.of());

        assertThat(call).isInstanceOf(NumberElement.class);
        assertThat(call.getType()).isEqualTo(Type.NUMBER);
        assertThat(((NumberElement) call).getValue()).isBetween(0.0, 1.0);
    }

}
