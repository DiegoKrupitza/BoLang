package com.diegokrupitza.bolang.vm.types;

import com.diegokrupitza.bolang.vm.utils.Arrays;
import com.diegokrupitza.bolang.vm.utils.Booleans;
import com.diegokrupitza.bolang.vm.utils.Types;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 15.07.21
 */
public class TypesTest {

    private static Stream<Arguments> isBothOfTypeSource() {
        return Stream.of(
                Arguments.of(Type.DOUBLE, new DoubleElement(1.0), new DoubleElement(2.0), true),
                Arguments.of(Type.DOUBLE, new DoubleElement(1.0), new IntegerElement(2), false),
                Arguments.of(Type.DOUBLE, new DoubleElement(1.0), Booleans.TRUE, false),
                Arguments.of(Type.DOUBLE, new DoubleElement(1.0), Booleans.FALSE, false),
                Arguments.of(Type.DOUBLE, new DoubleElement(1.0), Arrays.emptyArray(), false),
                Arguments.of(Type.DOUBLE, new DoubleElement(1.0), new StringElement(""), false),

                Arguments.of(Type.INTEGER_NUMBER, new IntegerElement(1000), new DoubleElement(2.0), false),
                Arguments.of(Type.INTEGER_NUMBER, new IntegerElement(1000), new IntegerElement(2), true),
                Arguments.of(Type.INTEGER_NUMBER, new IntegerElement(1000), Booleans.TRUE, false),
                Arguments.of(Type.INTEGER_NUMBER, new IntegerElement(1000), Booleans.FALSE, false),
                Arguments.of(Type.INTEGER_NUMBER, new IntegerElement(1000), Arrays.emptyArray(), false),
                Arguments.of(Type.INTEGER_NUMBER, new IntegerElement(1000), new StringElement(""), false),

                Arguments.of(Type.BOOLEAN, Booleans.FALSE, new DoubleElement(2.0), false),
                Arguments.of(Type.BOOLEAN, Booleans.FALSE, new IntegerElement(2), false),
                Arguments.of(Type.BOOLEAN, Booleans.FALSE, Booleans.TRUE, true),
                Arguments.of(Type.BOOLEAN, Booleans.FALSE, Booleans.FALSE, true),
                Arguments.of(Type.BOOLEAN, Booleans.FALSE, Arrays.emptyArray(), false),
                Arguments.of(Type.BOOLEAN, Booleans.FALSE, new StringElement(""), false),

                Arguments.of(Type.BOOLEAN, Booleans.TRUE, new DoubleElement(2.0), false),
                Arguments.of(Type.BOOLEAN, Booleans.TRUE, new IntegerElement(2), false),
                Arguments.of(Type.BOOLEAN, Booleans.TRUE, Booleans.TRUE, true),
                Arguments.of(Type.BOOLEAN, Booleans.TRUE, Booleans.FALSE, true),
                Arguments.of(Type.BOOLEAN, Booleans.TRUE, Arrays.emptyArray(), false),
                Arguments.of(Type.BOOLEAN, Booleans.TRUE, new StringElement(""), false),


                Arguments.of(Type.ARRAY, Arrays.emptyArray(), new DoubleElement(2.0), false),
                Arguments.of(Type.ARRAY, Arrays.emptyArray(), new IntegerElement(2), false),
                Arguments.of(Type.ARRAY, Arrays.emptyArray(), Booleans.TRUE, false),
                Arguments.of(Type.ARRAY, Arrays.emptyArray(), Booleans.FALSE, false),
                Arguments.of(Type.ARRAY, Arrays.emptyArray(), Arrays.emptyArray(), true),
                Arguments.of(Type.ARRAY, Arrays.emptyArray(), new StringElement(""), false),

                Arguments.of(Type.STRING, new StringElement("Hey Hey Hey"), new DoubleElement(2.0), false),
                Arguments.of(Type.STRING, new StringElement("Hey Hey Hey"), new IntegerElement(2), false),
                Arguments.of(Type.STRING, new StringElement("Hey Hey Hey"), Booleans.TRUE, false),
                Arguments.of(Type.STRING, new StringElement("Hey Hey Hey"), Booleans.FALSE, false),
                Arguments.of(Type.STRING, new StringElement("Hey Hey Hey"), Arrays.emptyArray(), false),
                Arguments.of(Type.STRING, new StringElement("Hey Hey Hey"), new StringElement(""), true)
        );
    }

    private static Stream<Arguments> isNotOfTypeSource() {
        return Stream.of(
                Arguments.of(Type.INTEGER_NUMBER, new IntegerElement(1), false),
                Arguments.of(Type.VOID, new IntegerElement(1), true),
                Arguments.of(Type.DOUBLE, new IntegerElement(1), true),
                Arguments.of(Type.ARRAY, new IntegerElement(1), true),
                Arguments.of(Type.BOOLEAN, new IntegerElement(1), true),
                Arguments.of(Type.STRING, new IntegerElement(1), true),

                Arguments.of(Type.INTEGER_NUMBER, new DoubleElement(1.0), true),
                Arguments.of(Type.VOID, new DoubleElement(1.0), true),
                Arguments.of(Type.DOUBLE, new DoubleElement(1.0), false),
                Arguments.of(Type.ARRAY, new DoubleElement(1.0), true),
                Arguments.of(Type.BOOLEAN, new DoubleElement(1.0), true),
                Arguments.of(Type.STRING, new DoubleElement(1.0), true),

                Arguments.of(Type.INTEGER_NUMBER, VoidElement.NO_VALUE, true),
                Arguments.of(Type.VOID, VoidElement.NO_VALUE, false),
                Arguments.of(Type.DOUBLE, VoidElement.NO_VALUE, true),
                Arguments.of(Type.ARRAY, VoidElement.NO_VALUE, true),
                Arguments.of(Type.BOOLEAN, VoidElement.NO_VALUE, true),
                Arguments.of(Type.STRING, VoidElement.NO_VALUE, true),

                Arguments.of(Type.INTEGER_NUMBER, Arrays.emptyArray(), true),
                Arguments.of(Type.VOID, Arrays.emptyArray(), true),
                Arguments.of(Type.DOUBLE, Arrays.emptyArray(), true),
                Arguments.of(Type.ARRAY, Arrays.emptyArray(), false),
                Arguments.of(Type.BOOLEAN, Arrays.emptyArray(), true),
                Arguments.of(Type.STRING, Arrays.emptyArray(), true),

                Arguments.of(Type.INTEGER_NUMBER, Arrays.emptyArray(), true),
                Arguments.of(Type.VOID, Arrays.emptyArray(), true),
                Arguments.of(Type.DOUBLE, Arrays.emptyArray(), true),
                Arguments.of(Type.ARRAY, Arrays.emptyArray(), false),
                Arguments.of(Type.BOOLEAN, Arrays.emptyArray(), true),
                Arguments.of(Type.STRING, Arrays.emptyArray(), true),

                Arguments.of(Type.INTEGER_NUMBER, Booleans.TRUE, true),
                Arguments.of(Type.VOID, Booleans.TRUE, true),
                Arguments.of(Type.DOUBLE, Booleans.TRUE, true),
                Arguments.of(Type.ARRAY, Booleans.TRUE, true),
                Arguments.of(Type.BOOLEAN, Booleans.TRUE, false),
                Arguments.of(Type.STRING, Booleans.TRUE, true),

                Arguments.of(Type.INTEGER_NUMBER, Booleans.FALSE, true),
                Arguments.of(Type.VOID, Booleans.FALSE, true),
                Arguments.of(Type.DOUBLE, Booleans.FALSE, true),
                Arguments.of(Type.ARRAY, Booleans.FALSE, true),
                Arguments.of(Type.BOOLEAN, Booleans.FALSE, false),
                Arguments.of(Type.STRING, Booleans.FALSE, true),

                Arguments.of(Type.INTEGER_NUMBER, new StringElement("test"), true),
                Arguments.of(Type.VOID, new StringElement("test"), true),
                Arguments.of(Type.DOUBLE, new StringElement("test"), true),
                Arguments.of(Type.ARRAY, new StringElement("test"), true),
                Arguments.of(Type.BOOLEAN, new StringElement("test"), true),
                Arguments.of(Type.STRING, new StringElement("test"), false)
        );
    }


    @ParameterizedTest
    @MethodSource("isBothOfTypeSource")
    void isBothOfTypeTest(Type type, AbstractElementType<?> e1, AbstractElementType<?> e2, boolean erg) {
        assertThat(Types.isBothOfType(type, e1, e2)).isEqualTo(erg);
    }

    @ParameterizedTest
    @MethodSource("isNotOfTypeSource")
    void isNotOfTypeTest(Type type, AbstractElementType<?> elem, boolean res) {
        assertThat(Types.isNotOfType(type, elem)).isEqualTo(res);
    }
}
