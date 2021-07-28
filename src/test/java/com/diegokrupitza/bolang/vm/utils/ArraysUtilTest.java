package com.diegokrupitza.bolang.vm.utils;

import com.diegokrupitza.bolang.vm.types.ArrayElement;
import com.diegokrupitza.bolang.vm.types.DoubleElement;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
public class ArraysUtilTest {

    @Test
    public void generateEmptyArrayTest() {
        ArrayElement arrayElement = Arrays.emptyArray();
        assertThat(arrayElement.getValue()).isEmpty();
    }

    @Test
    public void isEmptyArrayTest() {
        ArrayElement emptyArray = Arrays.emptyArray();
        assertThat(Arrays.isEmpty(emptyArray)).isTrue();

        ArrayElement notEmptyArray = new ArrayElement(Collections.singletonList(new DoubleElement(1.9)));
        assertThat(Arrays.isEmpty(notEmptyArray)).isFalse();
    }

}
