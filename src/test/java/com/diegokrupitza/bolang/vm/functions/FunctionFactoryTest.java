package com.diegokrupitza.bolang.vm.functions;

import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionNotFoundException;
import com.diegokrupitza.bolang.vm.functions.impl.numbers.ToIntFunction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
public class FunctionFactoryTest {

    @Test
    public void ToIntFactoryTest() {
        Function toInt = FunctionFactory.getFunction("Numbers", "toInt");
        assertThat(toInt).isInstanceOf(ToIntFunction.class);
    }

    @Test
    public void invalidFactoryTest() {
        assertThatThrownBy(() ->
                FunctionFactory.getFunction("WRONG", "thisFunctionWillNeverExistsSoPleaseDoNotCallThisAAAAAAAAAAAAAAAAAh")
        )
                .isInstanceOf(BoFunctionNotFoundException.class)
                .hasMessage("The function with the name thisFunctionWillNeverExistsSoPleaseDoNotCallThisAAAAAAAAAAAAAAAAAh in module WRONG does not exist!");

    }

}
