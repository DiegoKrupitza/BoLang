package com.diegokrupitza.bolang;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 04.08.21
 */
public class BoLangTest {

    private final static ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final static ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final static PrintStream originalOut = System.out;
    private final static PrintStream originalErr = System.err;

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

    @Test
    void runExamplesHelloWorldTest() {
        BoLang.main(new String[]{"examples/HelloWorld.bo"});

        assertThat(outContent.toString().trim()).isEqualTo("\"Hello World!\"");
    }

    @Test
    void runExamplesSelfDefinedFunctionsInvalidTest() {
        BoLang.main(new String[]{"examples/SelfDefinedFunction.bo"});

        assertThat(errContent.toString().trim()).contains("You are currently in `non function` mode! This means you are not allowed to have self");
    }

    @Test
    void runExamplesSelfDefinedFunctionsValidTest() {
        BoLang.main(new String[]{"examples/SelfDefinedFunction.bo", "-f"});

        assertThat(outContent.toString().trim()).contains("8");
    }

    @Test
    void runExampleProject() {
        BoLang.main(new String[]{"examples/sampleProject", "-f"});

        assertThat(outContent.toString().trim()).contains("3");
    }

    @Test
    void runExampleProjectToCallModule() {
        BoLang.main(new String[]{"examples/sampleProject", "-f", "-p", "{\"p1\":\"1\",\"param2\":\"X\"}"});

        assertThat(outContent.toString().trim()).contains("____\n" +
                "|DD|____T_\n" +
                "|_ |_____|<\n" +
                "@-@-@-oo\\);".trim());
    }

    @Test
    void showHelpTest() {
        BoLang.main(new String[]{"-h"});

        assertThat(outContent.toString().trim()).contains("usage: BoLang");
    }

    @Test
    void showVersionTest() {
        BoLang.main(new String[]{"-v"});

        assertThat(outContent.toString().trim()).contains("Version: ");
    }
}
