package com.diegokrupitza.bolang.vm.functions.impl.date;

import com.diegokrupitza.bolang.vm.functions.Function;
import com.diegokrupitza.bolang.vm.functions.FunctionFactory;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionParameterException;
import com.diegokrupitza.bolang.vm.types.*;
import com.diegokrupitza.bolang.vm.utils.Booleans;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
public class DateFunctionTest {

    private Function date;

    @BeforeEach
    public void init() {
        date = FunctionFactory.getFunction("Date", "date");
    }

    @Test
    public void checkParamsValidTest() {
        StringElement stringElement = new StringElement("dd-MM-YYYY");

        date.paramCheck(Collections.singletonList(stringElement));
    }

    @Test
    public void checkParamsInvalidTest() {

        // too many params
        assertThatThrownBy(() -> date.paramCheck(Arrays.asList(new StringElement(""), new StringElement(""))))
                .isInstanceOf(BoFunctionParameterException.class)
                .hasMessage("The function `date` is only allowed to be called with a string parameter that describes the format or no parameter");

        // invalid type of param
        assertThatThrownBy(() -> date.paramCheck(Collections.singletonList(new IntegerElement(1))))
                .isInstanceOf(BoFunctionParameterException.class)
                .hasMessage("The function `date` is only allowed to be called with a string parameter that describes the format or no parameter");

        // invalid type of param
        assertThatThrownBy(() -> date.paramCheck(Collections.singletonList(new DoubleElement(1.0))))
                .isInstanceOf(BoFunctionParameterException.class)
                .hasMessage("The function `date` is only allowed to be called with a string parameter that describes the format or no parameter");

        // invalid type of param
        assertThatThrownBy(() -> date.paramCheck(Collections.singletonList(Booleans.FALSE)))
                .isInstanceOf(BoFunctionParameterException.class)
                .hasMessage("The function `date` is only allowed to be called with a string parameter that describes the format or no parameter");

        // invalid type of param
        assertThatThrownBy(() -> date.paramCheck(Collections.singletonList(Booleans.TRUE)))
                .isInstanceOf(BoFunctionParameterException.class)
                .hasMessage("The function `date` is only allowed to be called with a string parameter that describes the format or no parameter");

        // invalid type of param
        assertThatThrownBy(() -> date.paramCheck(Collections.singletonList(com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray())))
                .isInstanceOf(BoFunctionParameterException.class)
                .hasMessage("The function `date` is only allowed to be called with a string parameter that describes the format or no parameter");
    }

    @Test
    public void checkParamsInvalidTooManyTest() {
        DoubleElement doubleElement1 = new DoubleElement(12.12);
        DoubleElement doubleElement2 = new DoubleElement(99.12);

        assertThatThrownBy(() -> date.paramCheck(Arrays.asList(doubleElement1, doubleElement2)))
                .isInstanceOf(BoFunctionParameterException.class)
                .hasMessage("The function `date` is only allowed to be called with a string parameter that describes the format or no parameter");
    }


    @Test
    public void dateWithOutParamTest() {
        AbstractElementType<?> call = date.call(new ArrayList<>());

        assertThat(call).isInstanceOf(StringElement.class);
        assertThat(call.getType()).isEqualTo(Type.STRING);
        assertThat(((StringElement) call).getValue()).containsIgnoringCase("" + LocalDateTime.now().getYear());
    }

    @Test
    public void dateWithParamTest() {
        AbstractElementType<?> call = date.call(Collections.singletonList(new StringElement("YYYY")));

        assertThat(call).isInstanceOf(StringElement.class);
        assertThat(call.getType()).isEqualTo(Type.STRING);
        assertThat(((StringElement) call).getValue()).containsIgnoringCase("" + LocalDateTime.now().getYear());
    }

}
