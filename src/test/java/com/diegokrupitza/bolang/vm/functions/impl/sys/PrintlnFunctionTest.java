package com.diegokrupitza.bolang.vm.functions.impl.sys;

import com.diegokrupitza.bolang.vm.functions.Function;
import com.diegokrupitza.bolang.vm.functions.FunctionFactory;
import com.diegokrupitza.bolang.vm.types.AbstractElementType;
import com.diegokrupitza.bolang.vm.types.IntegerElement;
import com.diegokrupitza.bolang.vm.types.StringElement;
import com.diegokrupitza.bolang.vm.types.VoidElement;
import com.diegokrupitza.bolang.vm.utils.Arrays;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
public class PrintlnFunctionTest {

    private final static ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final static ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final static PrintStream originalOut = System.out;
    private final static PrintStream originalErr = System.err;

    private Function println;

    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @SneakyThrows
    @AfterEach
    public void cleanUpStream() {
        outContent.flush();
        outContent.reset();
    }

    @BeforeEach
    public void init() {
        println = FunctionFactory.getFunction("Sys", "println");
    }

    @Test
    public void singleParamOutStringTest() {
        AbstractElementType<?> call = println.call(List.of(new StringElement("Hey You!")));

        assertThat(call).isEqualTo(VoidElement.NO_VALUE);
        assertThat(outContent.toString()).isEqualTo("Hey You!\n");
    }

    @Test
    public void multipleParamOutStringTest() {
        AbstractElementType<?> call = println.call(List.of(new StringElement("Hey You!"), new StringElement("Test2!")));

        assertThat(call).isEqualTo(VoidElement.NO_VALUE);
        assertThat(outContent.toString()).isEqualTo("Hey You!Test2!\n");
    }

    @Test
    public void singleParamIntTest() {
        AbstractElementType<?> call = println.call(List.of(new IntegerElement(100)));

        assertThat(call).isEqualTo(VoidElement.NO_VALUE);
        assertThat(outContent.toString()).isEqualTo("100\n");
    }

    @Test
    public void multipleParamTest() {
        AbstractElementType<?> call = println.call(List.of(new IntegerElement(100), new IntegerElement(101)));

        assertThat(call).isEqualTo(VoidElement.NO_VALUE);
        assertThat(outContent.toString()).isEqualTo("100101\n");
    }

    @Test
    public void singleArrayTest() {
        AbstractElementType<?> call = println.call(List.of(Arrays.emptyArray()));

        assertThat(call).isEqualTo(VoidElement.NO_VALUE);
        assertThat(outContent.toString()).isEqualTo("[]\n");
    }

}
