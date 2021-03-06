package com.diegokrupitza.bolang.vm.types;

import com.diegokrupitza.bolang.vm.VirtualMachineException;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
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
public class ElementTypeTest {

    private static Stream<Arguments> equalsTestSource() {
        return Stream.of(
                Arguments.of(new IntegerElement(10), new IntegerElement(10), true),
                Arguments.of(new IntegerElement(11), new IntegerElement(10), false),
                Arguments.of(new IntegerElement(10), new DoubleElement(10.0), true),
                Arguments.of(new IntegerElement(10), new StringElement("HelloWorld"), false),
                Arguments.of(new IntegerElement(10), new BooleanElement(false), false),
                Arguments.of(new IntegerElement(10), new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), false),


                Arguments.of(new DoubleElement(10.0), new IntegerElement(10), true),
                Arguments.of(new DoubleElement(11.0), new IntegerElement(10), false),
                Arguments.of(new DoubleElement(10.0), new DoubleElement(10.0), true),
                Arguments.of(new DoubleElement(10.0), new StringElement("HelloWorld"), false),
                Arguments.of(new DoubleElement(10.0), new BooleanElement(false), false),
                Arguments.of(new DoubleElement(10.0), new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), false),

                Arguments.of(new StringElement("HelloWorld"), new IntegerElement(10), false),
                Arguments.of(new StringElement("HelloWorld"), new IntegerElement(10), false),
                Arguments.of(new StringElement("HelloWorld"), new DoubleElement(10.0), false),
                Arguments.of(new StringElement("HelloWorld"), new StringElement("HelloWorld"), true),
                Arguments.of(new StringElement("HelloWorld"), new BooleanElement(false), false),
                Arguments.of(new StringElement("HelloWorld"), new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), false),

                Arguments.of(new BooleanElement(false), new IntegerElement(10), false),
                Arguments.of(new BooleanElement(false), new IntegerElement(10), false),
                Arguments.of(new BooleanElement(false), new DoubleElement(10.0), false),
                Arguments.of(new BooleanElement(false), new StringElement("HelloWorld"), false),
                Arguments.of(new BooleanElement(false), new BooleanElement(false), true),
                Arguments.of(new BooleanElement(false), new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), false),

                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), new IntegerElement(10), false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), new IntegerElement(10), false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), new DoubleElement(10.0), false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), new StringElement("HelloWorld"), false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), new BooleanElement(false), false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), true),

                Arguments.of(VoidElement.NO_VALUE, VoidElement.NO_VALUE, true),
                Arguments.of(VoidElement.NO_VALUE, new IntegerElement(10), false),
                Arguments.of(VoidElement.NO_VALUE, new DoubleElement(10.0), false),
                Arguments.of(VoidElement.NO_VALUE, new StringElement("HelloWorld"), false),
                Arguments.of(VoidElement.NO_VALUE, new BooleanElement(false), false),
                Arguments.of(VoidElement.NO_VALUE, new BooleanElement(true), false),
                Arguments.of(VoidElement.NO_VALUE, new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), false)
        );
    }

    private static Stream<Arguments> hashCodeSource() {
        return Stream.of(
                Arguments.of(new IntegerElement(10), new IntegerElement(10), true),
                Arguments.of(new IntegerElement(11), new IntegerElement(10), false),
                Arguments.of(new IntegerElement(10), new DoubleElement(10.0), false),
                Arguments.of(new IntegerElement(10), new StringElement("HelloWorld"), false),
                Arguments.of(new IntegerElement(10), new BooleanElement(false), false),
                Arguments.of(new IntegerElement(10), new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), false),


                Arguments.of(new DoubleElement(10.0), new IntegerElement(10), false),
                Arguments.of(new DoubleElement(11.0), new IntegerElement(10), false),
                Arguments.of(new DoubleElement(10.0), new DoubleElement(10.0), true),
                Arguments.of(new DoubleElement(10.0), new StringElement("HelloWorld"), false),
                Arguments.of(new DoubleElement(10.0), new BooleanElement(false), false),
                Arguments.of(new DoubleElement(10.0), new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), false),

                Arguments.of(new StringElement("HelloWorld"), new IntegerElement(10), false),
                Arguments.of(new StringElement("HelloWorld"), new IntegerElement(10), false),
                Arguments.of(new StringElement("HelloWorld"), new DoubleElement(10.0), false),
                Arguments.of(new StringElement("HelloWorld"), new StringElement("HelloWorld"), true),
                Arguments.of(new StringElement("HelloWorld"), new BooleanElement(false), false),
                Arguments.of(new StringElement("HelloWorld"), new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), false),

                Arguments.of(new BooleanElement(false), new IntegerElement(10), false),
                Arguments.of(new BooleanElement(false), new IntegerElement(10), false),
                Arguments.of(new BooleanElement(false), new DoubleElement(10.0), false),
                Arguments.of(new BooleanElement(false), new StringElement("HelloWorld"), false),
                Arguments.of(new BooleanElement(false), new BooleanElement(false), true),
                Arguments.of(new BooleanElement(false), new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), false),

                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), new IntegerElement(10), false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), new IntegerElement(10), false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), new DoubleElement(10.0), false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), new StringElement("HelloWorld"), false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), new BooleanElement(false), false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), true),

                Arguments.of(VoidElement.NO_VALUE, VoidElement.NO_VALUE, true),
                Arguments.of(VoidElement.NO_VALUE, new IntegerElement(10), false),
                Arguments.of(VoidElement.NO_VALUE, new DoubleElement(10.0), false),
                Arguments.of(VoidElement.NO_VALUE, new StringElement("HelloWorld"), false),
                Arguments.of(VoidElement.NO_VALUE, new BooleanElement(false), false),
                Arguments.of(VoidElement.NO_VALUE, new BooleanElement(true), false),
                Arguments.of(VoidElement.NO_VALUE, new ArrayElement(Collections.singletonList(new StringElement("HelloWorld"))), false)
        );
    }

    private static Stream<Arguments> checkTypeNamesSource() {
        return Stream.of(
                Arguments.of(Type.DOUBLE, "Double"),
                Arguments.of(Type.INTEGER_NUMBER, "Integer"),
                Arguments.of(Type.STRING, "String"),
                Arguments.of(Type.ARRAY, "Array"),
                Arguments.of(Type.BOOLEAN, "Boolean"),
                Arguments.of(Type.VOID, "Void")
        );
    }

    private static Stream<Arguments> checkGetAtIndexStringSource() {
        return Stream.of(
                Arguments.of(new StringElement("ThisTest"), -2, true, ""),
                Arguments.of(new StringElement("ThisTest"), -1, true, ""),
                Arguments.of(new StringElement("ThisTest"), 0, false, "T"),
                Arguments.of(new StringElement("ThisTest"), 1, false, "h"),
                Arguments.of(new StringElement("ThisTest"), 2, false, "i"),
                Arguments.of(new StringElement("ThisTest"), 3, false, "s"),
                Arguments.of(new StringElement("ThisTest"), 4, false, "T"),
                Arguments.of(new StringElement("ThisTest"), 5, false, "e"),
                Arguments.of(new StringElement("ThisTest"), 6, false, "s"),
                Arguments.of(new StringElement("ThisTest"), 7, false, "t"),
                Arguments.of(new StringElement("ThisTest"), 8, true, ""),
                Arguments.of(new StringElement("ThisTest"), 9, true, ""),
                Arguments.of(new StringElement("ThisTest"), 10, true, "")
        );
    }

    @Test
    public void initStringTest() {
        String value = "Hello World!";
        StringElement stringElement = new StringElement(value);

        assertThat(stringElement.getType()).isEqualTo(Type.STRING);
        assertThat(stringElement.getValue()).isInstanceOf(Type.STRING.getValueClass());
        assertThat(stringElement.getValue()).isEqualTo(value);
    }

    @Test
    public void initNumberTest() {
        double value = 17.1999;
        DoubleElement doubleElement = new DoubleElement(value);

        assertThat(doubleElement.getType()).isEqualTo(Type.DOUBLE);
        assertThat(doubleElement.getValue()).isInstanceOf(Type.DOUBLE.getValueClass());
        assertThat(doubleElement.getValue()).isEqualTo(value);
    }

    @Test
    public void initIntegerTest() {
        int value = 21;
        IntegerElement integerElement = new IntegerElement(value);

        assertThat(integerElement.getType()).isEqualTo(Type.INTEGER_NUMBER);
        assertThat(integerElement.getValue()).isInstanceOf(Type.INTEGER_NUMBER.getValueClass());
        assertThat(integerElement.getValue()).isEqualTo(value);
    }

    @Test
    public void initArrayTest() {
        StringElement stringElement = new StringElement("Hello");
        IntegerElement integerElement = new IntegerElement(12);
        DoubleElement doubleElement = new DoubleElement(12.12);

        ArrayElement arrayElement = new ArrayElement(Arrays.asList(stringElement, integerElement, doubleElement));

        assertThat(arrayElement.getType()).isEqualTo(Type.ARRAY);

        @NonNull List<AbstractElementType<?>> value = arrayElement.getValue();

        assertThat(value)
                .isNotEmpty()
                .hasSize(3);

        // first element check
        assertThat(value.get(0)).isInstanceOf(StringElement.class);
        assertThat(value.get(0).getType()).isEqualTo(Type.STRING);
        assertThat(value.get(0).getValue()).isEqualTo("Hello");

        // second element check
        assertThat(value.get(1)).isInstanceOf(IntegerElement.class);
        assertThat(value.get(1).getType()).isEqualTo(Type.INTEGER_NUMBER);
        assertThat(value.get(1).getValue()).isEqualTo(12);

        // third element check
        assertThat(value.get(2)).isInstanceOf(DoubleElement.class);
        assertThat(value.get(2).getType()).isEqualTo(Type.DOUBLE);
        assertThat(value.get(2).getValue()).isEqualTo(12.12);
    }

    @Test
    public void nestedArrayTest() {

        StringElement stringElement = new StringElement("Hello");
        IntegerElement integerElement = new IntegerElement(12);
        DoubleElement doubleElement = new DoubleElement(12.12);

        ArrayElement nestedArray = new ArrayElement(Arrays.asList(integerElement, doubleElement));

        ArrayElement arrayElement = new ArrayElement(Arrays.asList(stringElement, nestedArray));

        @NonNull List<AbstractElementType<?>> outerArray = arrayElement.getValue();
        assertThat(outerArray)
                .isNotEmpty()
                .hasSize(2);

        assertThat(outerArray.get(0)).isInstanceOf(StringElement.class);
        assertThat(outerArray.get(0).getType()).isEqualTo(Type.STRING);
        assertThat(outerArray.get(0).getValue()).isEqualTo("Hello");

        assertThat(outerArray.get(1)).isInstanceOf(ArrayElement.class);

        ArrayElement innerArray = (ArrayElement) (outerArray.get(1));

        // innerarray first element check
        assertThat(innerArray.getValue().get(0)).isInstanceOf(IntegerElement.class);
        assertThat(innerArray.getValue().get(0).getType()).isEqualTo(Type.INTEGER_NUMBER);
        assertThat(innerArray.getValue().get(0).getValue()).isEqualTo(12);

        // innerarray second element check
        assertThat(innerArray.getValue().get(1)).isInstanceOf(DoubleElement.class);
        assertThat(innerArray.getValue().get(1).getType()).isEqualTo(Type.DOUBLE);
        assertThat(innerArray.getValue().get(1).getValue()).isEqualTo(12.12);

    }

    @Test
    public void initEmptyArrayTest() {
        ArrayElement emptyArray = new ArrayElement();
        assertThat(emptyArray).isInstanceOf(Type.ARRAY.getValueClass());
        assertThat(emptyArray.getType()).isEqualTo(Type.ARRAY);
        assertThat(emptyArray.getValue()).isEmpty();
    }

    @Test
    public void toStringTest() {
        StringElement stringElement = new StringElement("Hello");
        IntegerElement integerElement = new IntegerElement(12);
        DoubleElement doubleElement = new DoubleElement(12.12);

        ArrayElement innerArray = new ArrayElement(Arrays.asList(stringElement, integerElement, doubleElement));

        ArrayElement secondInnerArray = new ArrayElement(Arrays.asList(stringElement, innerArray));

        ArrayElement bigArray = new ArrayElement(Arrays.asList(stringElement, secondInnerArray, innerArray));

        assertThat(stringElement.toString()).isEqualTo("\"Hello\"");
        assertThat(integerElement.toString()).isEqualTo("12");
        assertThat(doubleElement.toString()).isEqualTo("12.12");
        assertThat(innerArray.toString()).isEqualTo("[\"Hello\", 12, 12.12]");
        assertThat(secondInnerArray.toString()).isEqualTo("[\"Hello\", [\"Hello\", 12, 12.12]]");
        assertThat(bigArray.toString()).isEqualTo("[\"Hello\", [\"Hello\", [\"Hello\", 12, 12.12]], [\"Hello\", 12, 12.12]]");
    }

    @ParameterizedTest
    @MethodSource("equalsTestSource")
    public void equalsTest(AbstractElementType<?> e1, AbstractElementType<?> e2, Boolean valid) {
        assertThat(e1.equals(e2)).isEqualTo(valid);
    }

    @ParameterizedTest
    @MethodSource("checkTypeNamesSource")
    public void checkTypeNamesTest(Type type, String expectedName) {
        assertThat(type.getName()).isEqualTo(expectedName);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("checkGetAtIndexStringSource")
    public void checkGetAtIndexStringTest(StringElement stringElement, int index, boolean shouldThrow, String erg) {
        if (shouldThrow) {
            assertThatThrownBy(() -> stringElement.get(index))
                    .isInstanceOf(VirtualMachineException.class)
                    .hasMessageContaining("Index has to be in range 0 to");
        } else {
            assertThat(stringElement.get(index).getValue()).isEqualTo(erg);
        }
    }

    @Test
    @SneakyThrows
    public void checkEmptyStringInitTest() {
        StringElement emptyString = new StringElement();
        assertThat(emptyString.getValue()).isEqualTo("");
    }

    @ParameterizedTest
    @MethodSource("hashCodeSource")
    void hashCodeTest(AbstractElementType<?> e1, AbstractElementType<?> e2, Boolean valid) {
        int val1 = e1.hashCode();
        int val2 = e2.hashCode();

        assertThat(val1 == val2).isEqualTo(valid);
    }
}
