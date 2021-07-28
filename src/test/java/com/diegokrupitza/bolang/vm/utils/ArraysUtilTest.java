package com.diegokrupitza.bolang.vm.utils;

import com.diegokrupitza.bolang.vm.types.ArrayElement;
import com.diegokrupitza.bolang.vm.types.DoubleElement;
import com.diegokrupitza.bolang.vm.types.IntegerElement;
import com.diegokrupitza.bolang.vm.types.StringElement;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
class ArraysUtilTest {

    @Test
    void generateEmptyArrayTest() {
        ArrayElement arrayElement = Arrays.emptyArray();
        assertThat(arrayElement.getValue()).isEmpty();
    }

    @Test
    void isEmptyArrayTest() {
        ArrayElement emptyArray = Arrays.emptyArray();
        assertThat(Arrays.isEmpty(emptyArray)).isTrue();

        ArrayElement notEmptyArray = new ArrayElement(Collections.singletonList(new DoubleElement(1.9)));
        assertThat(Arrays.isEmpty(notEmptyArray)).isFalse();
    }

    @Test
    void createArrayOfTest() {
        ArrayElement arrayElement = Arrays.of(new StringElement("Test"), new IntegerElement(10));
        assertThat(arrayElement).isNotNull();
        assertThat(arrayElement.size()).isEqualTo(2);
    }

    @Test
    void isArrayTest() {
        IntegerElement integerElement = new IntegerElement(10);
        ArrayElement arrayElement = new ArrayElement();
        ArrayElement nullArray = null;

        assertThat(Arrays.isArray(integerElement)).isFalse();
        assertThat(Arrays.isArray(arrayElement)).isTrue();
        assertThat(Arrays.isArray(nullArray)).isFalse();
    }

    @Test
    void toStringArrayTest() {
        ArrayElement arrayElement = Arrays.of(new IntegerElement(10), new StringElement("Test"));
        String actual = Arrays.toString(arrayElement);

        assertThat(actual).isEqualToIgnoringWhitespace("[10,\"Test\"]");
    }

    @Test
    void toStringNestedArrayTest() {
        ArrayElement arrayElement = Arrays.of(new IntegerElement(10), Arrays.of(new StringElement("Test"), new IntegerElement(11)));
        String actual = Arrays.toString(arrayElement);

        assertThat(actual).isEqualToIgnoringWhitespace("[10, [\"Test\", 11]]");
    }
}
