package com.diegokrupitza.bolang.vm;

import com.diegokrupitza.bolang.syntaxtree.BuildAstVisitor;
import com.diegokrupitza.bolang.syntaxtree.nodes.BoNode;
import com.diegokrupitza.bolang.vm.types.*;
import com.diegokrupitza.bolang.vm.utils.Booleans;
import com.diegokrupitza.pdfgenerator.BoLexer;
import com.diegokrupitza.pdfgenerator.BoParser;
import lombok.SneakyThrows;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 10.07.21
 */
public class VirtualMachineTest {

    private static VirtualMachine getVirtualMachine(BoNode head) {
        return new VirtualMachine(head);
    }

    private static Stream<Arguments> mathInfixOperationTestsSource() {
        return Stream.of(
                Arguments.of("+", new IntegerElement(10), new IntegerElement(10), new IntegerElement(20), Type.INTEGER_NUMBER),
                Arguments.of("+", new DoubleElement(10.1), new DoubleElement(10.1), new DoubleElement(20.2), Type.DOUBLE),
                Arguments.of("+", new IntegerElement(10), new DoubleElement(10.1), new DoubleElement(20.1), Type.DOUBLE),
                Arguments.of("+", new DoubleElement(10.1), new IntegerElement(10), new DoubleElement(20.1), Type.DOUBLE),

                Arguments.of("-", new IntegerElement(10), new IntegerElement(10), new IntegerElement(0), Type.INTEGER_NUMBER),
                Arguments.of("-", new DoubleElement(10.1), new DoubleElement(10.1), new DoubleElement(0.0), Type.DOUBLE),
                Arguments.of("-", new IntegerElement(10), new DoubleElement(10.5), new DoubleElement(-0.5), Type.DOUBLE),
                Arguments.of("-", new DoubleElement(10.5), new IntegerElement(10), new DoubleElement(0.5), Type.DOUBLE),

                Arguments.of("*", new IntegerElement(10), new IntegerElement(10), new IntegerElement(100), Type.INTEGER_NUMBER),
                Arguments.of("*", new DoubleElement(10.1), new DoubleElement(10.1), new DoubleElement(102.01), Type.DOUBLE),
                Arguments.of("*", new IntegerElement(10), new DoubleElement(10.1), new DoubleElement(101.0), Type.DOUBLE),
                Arguments.of("*", new DoubleElement(10.1), new IntegerElement(10), new DoubleElement(101.0), Type.DOUBLE),

                Arguments.of("/", new IntegerElement(10), new IntegerElement(10), new IntegerElement(1), Type.INTEGER_NUMBER),
                Arguments.of("/", new DoubleElement(10.1), new DoubleElement(10.1), new DoubleElement(1.0), Type.DOUBLE),
                Arguments.of("/", new IntegerElement(10), new DoubleElement(10.1), new DoubleElement(0.99009901), Type.DOUBLE),
                Arguments.of("/", new DoubleElement(10.1), new IntegerElement(10), new DoubleElement(1.01), Type.DOUBLE)
        );
    }

    private static Stream<Arguments> mathInfixOperationTestsInvalidSource() {
        return Stream.of(
                Arguments.of("+", new IntegerElement(10), new StringElement("AAAH")),
                Arguments.of("+", new DoubleElement(10.1), new StringElement("AAAH")),
                Arguments.of("+", new StringElement("AAAH"), new DoubleElement(10.1)),

                Arguments.of("-", new IntegerElement(10), new StringElement("AAAH")),
                Arguments.of("-", new DoubleElement(10.1), new StringElement("AAAH")),
                Arguments.of("-", new StringElement("AAAH"), new DoubleElement(10.1)),

                Arguments.of("*", new IntegerElement(10), new StringElement("AAAH")),
                Arguments.of("*", new DoubleElement(10.1), new StringElement("AAAH")),
                Arguments.of("*", new StringElement("AAAH"), new DoubleElement(10.1)),

                Arguments.of("/", new IntegerElement(10), new StringElement("AAAH")),
                Arguments.of("/", new DoubleElement(10.1), new StringElement("AAAH")),
                Arguments.of("/", new StringElement("AAAH"), new DoubleElement(10.1))
        );
    }

    private static Stream<Arguments> mathInfixOperationArrayTestsSource() {
        return Stream.of(
                Arguments.of("+", new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new IntegerElement(1)
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(2), new IntegerElement(3), new IntegerElement(4), new DoubleElement(5.4)))
                ),

                Arguments.of("+", new IntegerElement(1),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(2), new IntegerElement(3), new IntegerElement(4), new DoubleElement(5.4)))
                ),

                Arguments.of("+",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4))),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(2), new IntegerElement(4), new IntegerElement(6), new DoubleElement(8.8)))
                ),

                Arguments.of("+",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4))),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(2), new IntegerElement(4), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                Arguments.of("+",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2))),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(2), new IntegerElement(4), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                Arguments.of("+",
                        new ArrayElement(),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                Arguments.of("+",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4))),
                        new ArrayElement(),
                        new ArrayElement(java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                // sub

                Arguments.of("-", new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new IntegerElement(1)
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(0), new IntegerElement(1), new IntegerElement(2), new DoubleElement(3.4)))
                ),

                Arguments.of("-", new IntegerElement(1),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(0), new IntegerElement(-1), new IntegerElement(-2), new DoubleElement(-3.4)))
                ),

                Arguments.of("-",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4))),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(0), new IntegerElement(0), new IntegerElement(0), new DoubleElement(0.0)))
                ),

                Arguments.of("-",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4))),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(0), new IntegerElement(0), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                Arguments.of("-",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2))),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(0), new IntegerElement(0), new IntegerElement(-3), new DoubleElement(-4.4)))
                ),

                Arguments.of("-",
                        new ArrayElement(),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(-1), new IntegerElement(-2), new IntegerElement(-3), new DoubleElement(-4.4)))
                ),

                Arguments.of("-",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4))),
                        new ArrayElement(),
                        new ArrayElement(java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                // division
                Arguments.of("/", new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new IntegerElement(1)
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                Arguments.of("/", new IntegerElement(1),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(1), new DoubleElement(0.5), new DoubleElement(0.33333), new DoubleElement(0.22727273)))
                ),

                Arguments.of("/",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4))),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(1), new IntegerElement(1), new DoubleElement(1.0)))
                ),

                Arguments.of("/",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4))),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(1), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                Arguments.of("/",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2))),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(1), new IntegerElement(0), new DoubleElement(0.0)))
                ),

                Arguments.of("/",
                        new ArrayElement(),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new DoubleElement(0.0), new DoubleElement(0.0), new DoubleElement(0.0), new DoubleElement(0.0)))
                ),

                Arguments.of("/",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4))),
                        new ArrayElement(),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                // multiplication
                Arguments.of("*", new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new IntegerElement(1)
                        , new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                Arguments.of("*", new IntegerElement(1),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                Arguments.of("*",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4))),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(4), new IntegerElement(9), new DoubleElement(19.36)))
                ),

                Arguments.of("*",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4))),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(4), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                Arguments.of("*",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2))),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(4), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                Arguments.of("*",
                        new ArrayElement(),
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                        , new ArrayElement(java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                ),

                Arguments.of("*",
                        new ArrayElement(
                                java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4))),
                        new ArrayElement(),
                        new ArrayElement(java.util.Arrays.asList(new IntegerElement(1), new IntegerElement(2), new IntegerElement(3), new DoubleElement(4.4)))
                )

        );
    }

    private static Stream<Arguments> negateUnarySource() {
        return Stream.of(
                Arguments.of(new DoubleElement(Math.PI), new DoubleElement(-Math.PI)),
                Arguments.of(new IntegerElement(99), new IntegerElement(-99)),
                Arguments.of(Booleans.TRUE, Booleans.FALSE),
                Arguments.of(Booleans.FALSE, Booleans.TRUE),
                Arguments.of(new StringElement("&:>7_=>=&EV"), new StringElement("YEAH BABY:)")),
                Arguments.of(new ArrayElement(
                                Arrays.asList(
                                        new DoubleElement(Math.PI),
                                        new IntegerElement(99),
                                        Booleans.TRUE,
                                        Booleans.FALSE,
                                        new StringElement("YEAH BABY:)")
                                )
                        ),
                        new ArrayElement(
                                Arrays.asList(
                                        new DoubleElement(-Math.PI),
                                        new IntegerElement(-99),
                                        Booleans.FALSE,
                                        Booleans.TRUE,
                                        new StringElement("&:>7_=>=&EV")
                                )
                        ))
        );
    }

    private static Stream<Arguments> stringConcatenationSource() {
        return Stream.of(
                Arguments.of(new IntegerElement(10), new IntegerElement(20), new StringElement("1020")),
                Arguments.of(new IntegerElement(10), new DoubleElement(2.2), new StringElement("102.2")),
                Arguments.of(new IntegerElement(10), Booleans.TRUE, new StringElement("10true")),
                Arguments.of(new IntegerElement(10), Booleans.FALSE, new StringElement("10false")),
                Arguments.of(new IntegerElement(10), new StringElement("Hello Wolrd"), new StringElement("10Hello Wolrd")),
                Arguments.of(new IntegerElement(10), new ArrayElement(Collections.singletonList(new IntegerElement(1))), new StringElement("10[1]")),

                Arguments.of(new DoubleElement(10.0), new IntegerElement(20), new StringElement("10.020")),
                Arguments.of(new DoubleElement(10.0), new DoubleElement(2.2), new StringElement("10.02.2")),
                Arguments.of(new DoubleElement(10.0), Booleans.TRUE, new StringElement("10.0true")),
                Arguments.of(new DoubleElement(10.0), Booleans.FALSE, new StringElement("10.0false")),
                Arguments.of(new DoubleElement(10.0), new StringElement("Hello Wolrd"), new StringElement("10.0Hello Wolrd")),
                Arguments.of(new DoubleElement(10.0), new ArrayElement(Collections.singletonList(new IntegerElement(1))), new StringElement("10.0[1]")),

                Arguments.of(Booleans.FALSE, new IntegerElement(20), new StringElement("false20")),
                Arguments.of(Booleans.FALSE, new DoubleElement(2.2), new StringElement("false2.2")),
                Arguments.of(Booleans.FALSE, Booleans.TRUE, new StringElement("falsetrue")),
                Arguments.of(Booleans.FALSE, Booleans.FALSE, new StringElement("falsefalse")),
                Arguments.of(Booleans.FALSE, new StringElement("Hello Wolrd"), new StringElement("falseHello Wolrd")),
                Arguments.of(Booleans.FALSE, new ArrayElement(Collections.singletonList(new IntegerElement(1))), new StringElement("false[1]")),

                Arguments.of(Booleans.TRUE, new IntegerElement(20), new StringElement("true20")),
                Arguments.of(Booleans.TRUE, new DoubleElement(2.2), new StringElement("true2.2")),
                Arguments.of(Booleans.TRUE, Booleans.TRUE, new StringElement("truetrue")),
                Arguments.of(Booleans.TRUE, Booleans.FALSE, new StringElement("truefalse")),
                Arguments.of(Booleans.TRUE, new StringElement("Hello Wolrd"), new StringElement("trueHello Wolrd")),
                Arguments.of(Booleans.TRUE, new ArrayElement(Collections.singletonList(new IntegerElement(1))), new StringElement("true[1]")),

                Arguments.of(new StringElement("Hey"), new IntegerElement(20), new StringElement("Hey20")),
                Arguments.of(new StringElement("Hey"), new DoubleElement(2.2), new StringElement("Hey2.2")),
                Arguments.of(new StringElement("Hey"), Booleans.TRUE, new StringElement("Heytrue")),
                Arguments.of(new StringElement("Hey"), Booleans.FALSE, new StringElement("Heyfalse")),
                Arguments.of(new StringElement("Hey"), new StringElement("Hello Wolrd"), new StringElement("HeyHello Wolrd")),
                Arguments.of(new StringElement("Hey"), new ArrayElement(Collections.singletonList(new IntegerElement(1))), new StringElement("Hey[1]")),

                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new IntegerElement(20), new StringElement("[\"Hey\"]20")),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new DoubleElement(2.2), new StringElement("[\"Hey\"]2.2")),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), Booleans.TRUE, new StringElement("[\"Hey\"]true")),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), Booleans.FALSE, new StringElement("[\"Hey\"]false")),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new StringElement("Hello Wolrd"), new StringElement("[\"Hey\"]Hello Wolrd")),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new ArrayElement(Collections.singletonList(new IntegerElement(1))), new StringElement("[\"Hey\"][1]"))

        );
    }

    public static Stream<Arguments> accessStringOrArraySource() {
        // AbstractElementType<?> elem, int index, AbstractElementType<?> erg
        return Stream.of(
                Arguments.of(new StringElement("First String"), 2, new StringElement("r")),
                Arguments.of(new StringElement("First String"), 8, new StringElement("r")),
                Arguments.of(new ArrayElement(Arrays.asList(new StringElement("First String"), new IntegerElement(10))), 0, new StringElement("First String")),
                Arguments.of(new ArrayElement(Arrays.asList(new StringElement("First String"), new IntegerElement(10))), 1, new IntegerElement(10))
        );
    }

    public static Stream<Arguments> invalidIndexAccessStringOrArraySource() {
        return Stream.of(
                Arguments.of(new StringElement("Hey"), new DoubleElement(100.0)),
                Arguments.of(new StringElement("Hey"), Booleans.TRUE),
                Arguments.of(new StringElement("Hey"), Booleans.FALSE),
                Arguments.of(new StringElement("Hey"), new StringElement("100.0")),
                Arguments.of(new StringElement("Hey"), com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray()),

                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new DoubleElement(100.0)),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), Booleans.TRUE),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), Booleans.FALSE),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new StringElement("100.0")),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray())
        );
    }

    public static Stream<Arguments> invalidTypeAccessIndexSource() {
        return Stream.of(
                Arguments.of(new StringElement("AAAAH"), false),
                Arguments.of(new IntegerElement(1), true),
                Arguments.of(new DoubleElement(1.0), true),
                Arguments.of(Booleans.FALSE, true),
                Arguments.of(Booleans.TRUE, true),
                Arguments.of(new ArrayElement(Collections.singletonList(new DoubleElement(1.0))), false)
        );
    }

    public static Stream<Arguments> accessVariableSource() {
        return Stream.of(
                Arguments.of(new IntegerElement(199999)),
                Arguments.of(new DoubleElement(199.999)),
                Arguments.of(new StringElement("Hey Hey")),
                Arguments.of(com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray()),
                Arguments.of(Booleans.TRUE),
                Arguments.of(Booleans.FALSE)
        );
    }

    public static Stream<Arguments> validLogicOperationSource() {
        return Stream.of(

                // logic and
                Arguments.of("&&", Booleans.FALSE, Booleans.FALSE, false),
                Arguments.of("&&", Booleans.TRUE, Booleans.FALSE, false),
                Arguments.of("&&", Booleans.FALSE, Booleans.TRUE, false),
                Arguments.of("&&", Booleans.TRUE, Booleans.TRUE, true),

                // logic or
                Arguments.of("||", Booleans.FALSE, Booleans.FALSE, false),
                Arguments.of("||", Booleans.TRUE, Booleans.FALSE, true),
                Arguments.of("||", Booleans.FALSE, Booleans.TRUE, true),
                Arguments.of("||", Booleans.TRUE, Booleans.TRUE, true)
        );
    }

    public static Stream<Arguments> invalidLogicOperationSource() {
        return Stream.of(

                // logic and
                Arguments.of("&&", Booleans.FALSE, Booleans.FALSE, false),
                Arguments.of("&&", Booleans.TRUE, Booleans.FALSE, false),
                Arguments.of("&&", Booleans.FALSE, Booleans.TRUE, false),
                Arguments.of("&&", Booleans.TRUE, Booleans.TRUE, false),

                // invalid ones
                Arguments.of("&&", new IntegerElement(1), Booleans.TRUE, true),
                Arguments.of("&&", new DoubleElement(1.0), Booleans.TRUE, true),
                Arguments.of("&&", new StringElement("1"), Booleans.TRUE, true),
                Arguments.of("&&", com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), Booleans.TRUE, true),

                Arguments.of("&&", new IntegerElement(1), Booleans.FALSE, true),
                Arguments.of("&&", new DoubleElement(1.0), Booleans.FALSE, true),
                Arguments.of("&&", new StringElement("1"), Booleans.FALSE, true),
                Arguments.of("&&", com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), Booleans.FALSE, true),


                // logic or
                Arguments.of("||", Booleans.FALSE, Booleans.FALSE, false),
                Arguments.of("||", Booleans.TRUE, Booleans.FALSE, false),
                Arguments.of("||", Booleans.FALSE, Booleans.TRUE, false),
                Arguments.of("||", Booleans.TRUE, Booleans.TRUE, false),


                // invalid ones
                Arguments.of("||", new IntegerElement(1), Booleans.TRUE, true),
                Arguments.of("||", new DoubleElement(1.0), Booleans.TRUE, true),
                Arguments.of("||", new StringElement("1"), Booleans.TRUE, true),
                Arguments.of("||", com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), Booleans.TRUE, true),

                Arguments.of("||", new IntegerElement(1), Booleans.FALSE, true),
                Arguments.of("||", new DoubleElement(1.0), Booleans.FALSE, true),
                Arguments.of("||", new StringElement("1"), Booleans.FALSE, true),
                Arguments.of("||", com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), Booleans.FALSE, true)

        );
    }

    public static Stream<Arguments> equalsSource() {
        return Stream.of(
                Arguments.of(new IntegerElement(10), new IntegerElement(10), true),
                Arguments.of(new IntegerElement(10), new IntegerElement(111), false),
                Arguments.of(new IntegerElement(10), new DoubleElement(10.0), true),
                Arguments.of(new IntegerElement(10), Booleans.TRUE, false),
                Arguments.of(new IntegerElement(10), Booleans.FALSE, false),
                Arguments.of(new IntegerElement(10), new StringElement("Hello Wolrd"), false),
                Arguments.of(new IntegerElement(10), new ArrayElement(Collections.singletonList(new IntegerElement(1))), false),

                Arguments.of(new DoubleElement(10.0), new IntegerElement(10), true),
                Arguments.of(new DoubleElement(10.0), new DoubleElement(10.0), true),
                Arguments.of(new DoubleElement(10.0), new DoubleElement(10.1), false),
                Arguments.of(new DoubleElement(10.0), Booleans.TRUE, false),
                Arguments.of(new DoubleElement(10.0), Booleans.FALSE, false),
                Arguments.of(new DoubleElement(10.0), new StringElement("Hello Wolrd"), false),
                Arguments.of(new DoubleElement(10.0), new ArrayElement(Collections.singletonList(new IntegerElement(1))), false),

                Arguments.of(Booleans.FALSE, new IntegerElement(20), false),
                Arguments.of(Booleans.FALSE, new DoubleElement(2.2), false),
                Arguments.of(Booleans.FALSE, Booleans.TRUE, false),
                Arguments.of(Booleans.FALSE, Booleans.FALSE, true),
                Arguments.of(Booleans.FALSE, new StringElement("Hello Wolrd"), false),
                Arguments.of(Booleans.FALSE, new ArrayElement(Collections.singletonList(new IntegerElement(1))), false),

                Arguments.of(Booleans.TRUE, new IntegerElement(20), false),
                Arguments.of(Booleans.TRUE, new DoubleElement(2.2), false),
                Arguments.of(Booleans.TRUE, Booleans.TRUE, true),
                Arguments.of(Booleans.TRUE, Booleans.FALSE, false),
                Arguments.of(Booleans.TRUE, new StringElement("Hello Wolrd"), false),
                Arguments.of(Booleans.TRUE, new ArrayElement(Collections.singletonList(new IntegerElement(1))), false),

                Arguments.of(new StringElement("Hey"), new IntegerElement(20), false),
                Arguments.of(new StringElement("Hey"), new DoubleElement(2.2), false),
                Arguments.of(new StringElement("Hey"), Booleans.TRUE, false),
                Arguments.of(new StringElement("Hey"), Booleans.FALSE, false),
                Arguments.of(new StringElement("Hey"), new StringElement("Hey"), true),
                Arguments.of(new StringElement("Hey"), new StringElement("Hey1"), false),
                Arguments.of(new StringElement("Hey"), new ArrayElement(Collections.singletonList(new IntegerElement(1))), false),

                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new IntegerElement(20), false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new DoubleElement(2.2), false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), Booleans.TRUE, false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), Booleans.FALSE, false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new StringElement("Hello Wolrd"), false),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new ArrayElement(Collections.singletonList(new StringElement("Hey"))), true)
        );
    }

    public static Stream<Arguments> notequalsSource() {
        return Stream.of(
                Arguments.of(new IntegerElement(10), new IntegerElement(10), false),
                Arguments.of(new IntegerElement(10), new IntegerElement(111), true),
                Arguments.of(new IntegerElement(10), new DoubleElement(10.0), false),
                Arguments.of(new IntegerElement(10), Booleans.TRUE, true),
                Arguments.of(new IntegerElement(10), Booleans.FALSE, true),
                Arguments.of(new IntegerElement(10), new StringElement("Hello Wolrd"), true),
                Arguments.of(new IntegerElement(10), new ArrayElement(Collections.singletonList(new IntegerElement(1))), true),

                Arguments.of(new DoubleElement(10.0), new IntegerElement(10), false),
                Arguments.of(new DoubleElement(10.0), new DoubleElement(10.0), false),
                Arguments.of(new DoubleElement(10.0), new DoubleElement(10.1), true),
                Arguments.of(new DoubleElement(10.0), Booleans.TRUE, true),
                Arguments.of(new DoubleElement(10.0), Booleans.FALSE, true),
                Arguments.of(new DoubleElement(10.0), new StringElement("Hello Wolrd"), true),
                Arguments.of(new DoubleElement(10.0), new ArrayElement(Collections.singletonList(new IntegerElement(1))), true),

                Arguments.of(Booleans.FALSE, new IntegerElement(20), true),
                Arguments.of(Booleans.FALSE, new DoubleElement(2.2), true),
                Arguments.of(Booleans.FALSE, Booleans.TRUE, true),
                Arguments.of(Booleans.FALSE, Booleans.FALSE, false),
                Arguments.of(Booleans.FALSE, new StringElement("Hello Wolrd"), true),
                Arguments.of(Booleans.FALSE, new ArrayElement(Collections.singletonList(new IntegerElement(1))), true),

                Arguments.of(Booleans.TRUE, new IntegerElement(20), true),
                Arguments.of(Booleans.TRUE, new DoubleElement(2.2), true),
                Arguments.of(Booleans.TRUE, Booleans.TRUE, false),
                Arguments.of(Booleans.TRUE, Booleans.FALSE, true),
                Arguments.of(Booleans.TRUE, new StringElement("Hello Wolrd"), true),
                Arguments.of(Booleans.TRUE, new ArrayElement(Collections.singletonList(new IntegerElement(1))), true),

                Arguments.of(new StringElement("Hey"), new IntegerElement(20), true),
                Arguments.of(new StringElement("Hey"), new DoubleElement(2.2), true),
                Arguments.of(new StringElement("Hey"), Booleans.TRUE, true),
                Arguments.of(new StringElement("Hey"), Booleans.FALSE, true),
                Arguments.of(new StringElement("Hey"), new StringElement("Hey"), false),
                Arguments.of(new StringElement("Hey"), new StringElement("Hey1"), true),
                Arguments.of(new StringElement("Hey"), new ArrayElement(Collections.singletonList(new IntegerElement(1))), true),

                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new IntegerElement(20), true),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new DoubleElement(2.2), true),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), Booleans.TRUE, true),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), Booleans.FALSE, true),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new StringElement("Hello Wolrd"), true),
                Arguments.of(new ArrayElement(Collections.singletonList(new StringElement("Hey"))), new ArrayElement(Collections.singletonList(new StringElement("Hey"))), false)
        );
    }

    public static Stream<Arguments> validCompareSource() {
        return Stream.of(

                // >=
                Arguments.of(">=", new DoubleElement(10.0), new DoubleElement(10.0), Booleans.TRUE),
                Arguments.of(">=", new DoubleElement(11.0), new DoubleElement(10.0), Booleans.TRUE),
                Arguments.of(">=", new DoubleElement(9.0), new DoubleElement(10.0), Booleans.FALSE),
                Arguments.of(">=", new DoubleElement(10.0), new IntegerElement(10), Booleans.TRUE),
                Arguments.of(">=", new DoubleElement(10.0), new IntegerElement(9), Booleans.TRUE),
                Arguments.of(">=", new DoubleElement(10.0), new IntegerElement(1000), Booleans.FALSE),

                Arguments.of(">=", new IntegerElement(10), new DoubleElement(10.0), Booleans.TRUE),
                Arguments.of(">=", new IntegerElement(11), new DoubleElement(10.0), Booleans.TRUE),
                Arguments.of(">=", new IntegerElement(0), new DoubleElement(10.0), Booleans.FALSE),
                Arguments.of(">=", new IntegerElement(10), new IntegerElement(10), Booleans.TRUE),
                Arguments.of(">=", new IntegerElement(10), new IntegerElement(9), Booleans.TRUE),
                Arguments.of(">=", new IntegerElement(10), new IntegerElement(1000), Booleans.FALSE),

                // >
                Arguments.of(">", new DoubleElement(10.0), new DoubleElement(10.0), Booleans.FALSE),
                Arguments.of(">", new DoubleElement(11.0), new DoubleElement(10.0), Booleans.TRUE),
                Arguments.of(">", new DoubleElement(9.0), new DoubleElement(10.0), Booleans.FALSE),
                Arguments.of(">", new DoubleElement(10.0), new IntegerElement(10), Booleans.FALSE),
                Arguments.of(">", new DoubleElement(10.0), new IntegerElement(9), Booleans.TRUE),
                Arguments.of(">", new DoubleElement(10.0), new IntegerElement(1000), Booleans.FALSE),

                Arguments.of(">", new IntegerElement(10), new DoubleElement(10.0), Booleans.FALSE),
                Arguments.of(">", new IntegerElement(11), new DoubleElement(10.0), Booleans.TRUE),
                Arguments.of(">", new IntegerElement(0), new DoubleElement(10.0), Booleans.FALSE),
                Arguments.of(">", new IntegerElement(10), new IntegerElement(10), Booleans.FALSE),
                Arguments.of(">", new IntegerElement(10), new IntegerElement(9), Booleans.TRUE),
                Arguments.of(">", new IntegerElement(10), new IntegerElement(1000), Booleans.FALSE),

                // <
                Arguments.of("<", new DoubleElement(10.0), new DoubleElement(10.0), Booleans.FALSE),
                Arguments.of("<", new DoubleElement(11.0), new DoubleElement(10.0), Booleans.FALSE),
                Arguments.of("<", new DoubleElement(9.0), new DoubleElement(10.0), Booleans.TRUE),
                Arguments.of("<", new DoubleElement(10.0), new IntegerElement(10), Booleans.FALSE),
                Arguments.of("<", new DoubleElement(10.0), new IntegerElement(9), Booleans.FALSE),
                Arguments.of("<", new DoubleElement(10.0), new IntegerElement(1000), Booleans.TRUE),

                Arguments.of("<", new IntegerElement(10), new DoubleElement(10.0), Booleans.FALSE),
                Arguments.of("<", new IntegerElement(11), new DoubleElement(10.0), Booleans.FALSE),
                Arguments.of("<", new IntegerElement(0), new DoubleElement(10.0), Booleans.TRUE),
                Arguments.of("<", new IntegerElement(10), new IntegerElement(10), Booleans.FALSE),
                Arguments.of("<", new IntegerElement(10), new IntegerElement(9), Booleans.FALSE),
                Arguments.of("<", new IntegerElement(10), new IntegerElement(1000), Booleans.TRUE),

                // <=
                Arguments.of("<=", new DoubleElement(10.0), new DoubleElement(10.0), Booleans.TRUE),
                Arguments.of("<=", new DoubleElement(11.0), new DoubleElement(10.0), Booleans.FALSE),
                Arguments.of("<=", new DoubleElement(9.0), new DoubleElement(10.0), Booleans.TRUE),
                Arguments.of("<=", new DoubleElement(10.0), new IntegerElement(10), Booleans.TRUE),
                Arguments.of("<=", new DoubleElement(10.0), new IntegerElement(9), Booleans.FALSE),
                Arguments.of("<=", new DoubleElement(10.0), new IntegerElement(1000), Booleans.TRUE),

                Arguments.of("<=", new IntegerElement(10), new DoubleElement(10.0), Booleans.TRUE),
                Arguments.of("<=", new IntegerElement(11), new DoubleElement(10.0), Booleans.FALSE),
                Arguments.of("<=", new IntegerElement(0), new DoubleElement(10.0), Booleans.TRUE),
                Arguments.of("<=", new IntegerElement(10), new IntegerElement(10), Booleans.TRUE),
                Arguments.of("<=", new IntegerElement(10), new IntegerElement(9), Booleans.FALSE),
                Arguments.of("<=", new IntegerElement(10), new IntegerElement(1000), Booleans.TRUE)
        );
    }

    public static Stream<Arguments> invalidCompareSource() {
        return Stream.of(

                // >=
                Arguments.of(">=", new IntegerElement(10), new StringElement(""), true),
                Arguments.of(">=", new IntegerElement(10), Booleans.TRUE, true),
                Arguments.of(">=", new IntegerElement(10), Booleans.FALSE, true),
                Arguments.of(">=", new IntegerElement(10), com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), true),
                Arguments.of(">=", new IntegerElement(10), new DoubleElement(11.0), false),
                Arguments.of(">=", new IntegerElement(10), new IntegerElement(11), false),

                Arguments.of(">=", new StringElement(""), new IntegerElement(10), true),
                Arguments.of(">=", Booleans.TRUE, new IntegerElement(10), true),
                Arguments.of(">=", Booleans.FALSE, new IntegerElement(10), true),
                Arguments.of(">=", com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), new IntegerElement(10), true),
                Arguments.of(">=", new DoubleElement(11.0), new IntegerElement(10), false),
                Arguments.of(">=", new IntegerElement(11), new IntegerElement(10), false),

                Arguments.of(">=", new DoubleElement(10.0), new StringElement(""), true),
                Arguments.of(">=", new DoubleElement(10.0), Booleans.TRUE, true),
                Arguments.of(">=", new DoubleElement(10.0), Booleans.FALSE, true),
                Arguments.of(">=", new DoubleElement(10.0), com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), true),
                Arguments.of(">=", new DoubleElement(10.0), new DoubleElement(11.0), false),
                Arguments.of(">=", new DoubleElement(10.0), new IntegerElement(11), false),

                Arguments.of(">=", new StringElement(""), new DoubleElement(10.0), true),
                Arguments.of(">=", Booleans.TRUE, new DoubleElement(10.0), true),
                Arguments.of(">=", Booleans.FALSE, new DoubleElement(10.0), true),
                Arguments.of(">=", com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), new DoubleElement(10.0), true),
                Arguments.of(">=", new DoubleElement(11.0), new DoubleElement(10.0), false),
                Arguments.of(">=", new IntegerElement(11), new DoubleElement(10.0), false),

                // >
                Arguments.of(">", new IntegerElement(10), new StringElement(""), true),
                Arguments.of(">", new IntegerElement(10), Booleans.TRUE, true),
                Arguments.of(">", new IntegerElement(10), Booleans.FALSE, true),
                Arguments.of(">", new IntegerElement(10), com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), true),
                Arguments.of(">", new IntegerElement(10), new DoubleElement(11.0), false),
                Arguments.of(">", new IntegerElement(10), new IntegerElement(11), false),

                Arguments.of(">", new StringElement(""), new IntegerElement(10), true),
                Arguments.of(">", Booleans.TRUE, new IntegerElement(10), true),
                Arguments.of(">", Booleans.FALSE, new IntegerElement(10), true),
                Arguments.of(">", com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), new IntegerElement(10), true),
                Arguments.of(">", new DoubleElement(11.0), new IntegerElement(10), false),
                Arguments.of(">", new IntegerElement(11), new IntegerElement(10), false),

                Arguments.of(">", new DoubleElement(10.0), new StringElement(""), true),
                Arguments.of(">", new DoubleElement(10.0), Booleans.TRUE, true),
                Arguments.of(">", new DoubleElement(10.0), Booleans.FALSE, true),
                Arguments.of(">", new DoubleElement(10.0), com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), true),
                Arguments.of(">", new DoubleElement(10.0), new DoubleElement(11.0), false),
                Arguments.of(">", new DoubleElement(10.0), new IntegerElement(11), false),

                Arguments.of(">", new StringElement(""), new DoubleElement(10.0), true),
                Arguments.of(">", Booleans.TRUE, new DoubleElement(10.0), true),
                Arguments.of(">", Booleans.FALSE, new DoubleElement(10.0), true),
                Arguments.of(">", com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), new DoubleElement(10.0), true),
                Arguments.of(">", new DoubleElement(11.0), new DoubleElement(10.0), false),
                Arguments.of(">", new IntegerElement(11), new DoubleElement(10.0), false),

                // <=
                Arguments.of("<=", new IntegerElement(10), new StringElement(""), true),
                Arguments.of("<=", new IntegerElement(10), Booleans.TRUE, true),
                Arguments.of("<=", new IntegerElement(10), Booleans.FALSE, true),
                Arguments.of("<=", new IntegerElement(10), com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), true),
                Arguments.of("<=", new IntegerElement(10), new DoubleElement(11.0), false),
                Arguments.of("<=", new IntegerElement(10), new IntegerElement(11), false),

                Arguments.of("<=", new StringElement(""), new IntegerElement(10), true),
                Arguments.of("<=", Booleans.TRUE, new IntegerElement(10), true),
                Arguments.of("<=", Booleans.FALSE, new IntegerElement(10), true),
                Arguments.of("<=", com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), new IntegerElement(10), true),
                Arguments.of("<=", new DoubleElement(11.0), new IntegerElement(10), false),
                Arguments.of("<=", new IntegerElement(11), new IntegerElement(10), false),

                Arguments.of("<=", new DoubleElement(10.0), new StringElement(""), true),
                Arguments.of("<=", new DoubleElement(10.0), Booleans.TRUE, true),
                Arguments.of("<=", new DoubleElement(10.0), Booleans.FALSE, true),
                Arguments.of("<=", new DoubleElement(10.0), com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), true),
                Arguments.of("<=", new DoubleElement(10.0), new DoubleElement(11.0), false),
                Arguments.of("<=", new DoubleElement(10.0), new IntegerElement(11), false),

                Arguments.of("<=", new StringElement(""), new DoubleElement(10.0), true),
                Arguments.of("<=", Booleans.TRUE, new DoubleElement(10.0), true),
                Arguments.of("<=", Booleans.FALSE, new DoubleElement(10.0), true),
                Arguments.of("<=", com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), new DoubleElement(10.0), true),
                Arguments.of("<=", new DoubleElement(11.0), new DoubleElement(10.0), false),
                Arguments.of("<=", new IntegerElement(11), new DoubleElement(10.0), false),

                // <
                Arguments.of("<", new IntegerElement(10), new StringElement(""), true),
                Arguments.of("<", new IntegerElement(10), Booleans.TRUE, true),
                Arguments.of("<", new IntegerElement(10), Booleans.FALSE, true),
                Arguments.of("<", new IntegerElement(10), com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), true),
                Arguments.of("<", new IntegerElement(10), new DoubleElement(11.0), false),
                Arguments.of("<", new IntegerElement(10), new IntegerElement(11), false),

                Arguments.of("<", new StringElement(""), new IntegerElement(10), true),
                Arguments.of("<", Booleans.TRUE, new IntegerElement(10), true),
                Arguments.of("<", Booleans.FALSE, new IntegerElement(10), true),
                Arguments.of("<", com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), new IntegerElement(10), true),
                Arguments.of("<", new DoubleElement(11.0), new IntegerElement(10), false),
                Arguments.of("<", new IntegerElement(11), new IntegerElement(10), false),

                Arguments.of("<", new DoubleElement(10.0), new StringElement(""), true),
                Arguments.of("<", new DoubleElement(10.0), Booleans.TRUE, true),
                Arguments.of("<", new DoubleElement(10.0), Booleans.FALSE, true),
                Arguments.of("<", new DoubleElement(10.0), com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), true),
                Arguments.of("<", new DoubleElement(10.0), new DoubleElement(11.0), false),
                Arguments.of("<", new DoubleElement(10.0), new IntegerElement(11), false),

                Arguments.of("<", new StringElement(""), new DoubleElement(10.0), true),
                Arguments.of("<", Booleans.TRUE, new DoubleElement(10.0), true),
                Arguments.of("<", Booleans.FALSE, new DoubleElement(10.0), true),
                Arguments.of("<", com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), new DoubleElement(10.0), true),
                Arguments.of("<", new DoubleElement(11.0), new DoubleElement(10.0), false),
                Arguments.of("<", new IntegerElement(11), new DoubleElement(10.0), false)
        );
    }

    private static Stream<Arguments> validExternalParamSource() {
        return Stream.of(
                Arguments.of(
                        Map.ofEntries(
                                entry("a", "b"),
                                entry("c", "d")
                        ),
                        "a",
                        new StringElement("b")
                ),

                Arguments.of(
                        Map.ofEntries(
                                entry("a", "b"),
                                entry("c", "d")
                        ),
                        "c",
                        new StringElement("d")
                ),
                Arguments.of(
                        Map.ofEntries(
                                entry("a", "b"),
                                entry("test", "test"),
                                entry("c", "d")
                        ),
                        "test",
                        new StringElement("test")
                )
        );
    }

    private static Stream<Arguments> invalidExternalParamSource() {
        return Stream.of(
                Arguments.of(
                        Map.ofEntries(
                                entry("a", "b"),
                                entry("c", "d")
                        ),
                        "a",
                        false
                ),

                Arguments.of(
                        Map.ofEntries(
                                entry("a", "b"),
                                entry("c", "d")
                        ),
                        "d",
                        true
                ),
                Arguments.of(
                        Map.ofEntries(
                                entry("a", "b"),
                                entry("test", "test"),
                                entry("c", "d")
                        ),
                        "lala",
                        true
                ),
                Arguments.of(
                        Map.of(),
                        "lala",
                        true
                )
        );
    }

    private static Stream<Arguments> simpleInvalidIfSource() {
        return Stream.of(
                Arguments.of(new StringElement("A"), true),
                Arguments.of(new IntegerElement(10), true),
                Arguments.of(new DoubleElement(10.0), true),
                Arguments.of(com.diegokrupitza.bolang.vm.utils.Arrays.emptyArray(), true),
                Arguments.of(Booleans.FALSE, false),
                Arguments.of(Booleans.TRUE, false)
        );
    }

    @SneakyThrows
    @Test
    public void simpleReturnTest() {
        var program = "return 1;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal.getType()).isEqualTo(Type.INTEGER_NUMBER);
        assertThat(returnVal).isInstanceOf(IntegerElement.class);
        assertThat(((IntegerElement) returnVal).getValue()).isEqualTo(1);
    }

    @SneakyThrows
    @Test
    public void divisionResultTypNumberTest() {
        var program = "return 1/2;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal.getType()).isEqualTo(Type.DOUBLE);
        assertThat(returnVal).isInstanceOf(DoubleElement.class);
        assertThat(((DoubleElement) returnVal).getValue()).isEqualTo(0.5);
    }

    @SneakyThrows
    @Test
    public void divisionResultTypIntegerTest() {
        var program = "return 4/2;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal.getType()).isEqualTo(Type.INTEGER_NUMBER);
        assertThat(returnVal).isInstanceOf(IntegerElement.class);
        assertThat(((IntegerElement) returnVal).getValue()).isEqualTo(2);
    }


    @SneakyThrows
    @Test
    public void firstRiskFullMinusOperationTest() {
        var program = "return --1;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal.getType()).isEqualTo(Type.INTEGER_NUMBER);
        assertThat(returnVal).isInstanceOf(IntegerElement.class);
        assertThat(((IntegerElement) returnVal).getValue()).isEqualTo(1);
    }

    @SneakyThrows
    @Test
    public void secondRiskFullMinusOperationTest() {
        var program = "return !-1;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal.getType()).isEqualTo(Type.INTEGER_NUMBER);
        assertThat(returnVal).isInstanceOf(IntegerElement.class);
        assertThat(((IntegerElement) returnVal).getValue()).isEqualTo(1);
    }

    @SneakyThrows
    @Test
    public void simpleReturnFalseTest() {
        var program = "return False;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal.getType()).isEqualTo(Type.BOOLEAN);
        assertThat(returnVal).isInstanceOf(BooleanElement.class);
        assertThat(((BooleanElement) returnVal).getValue()).isFalse();
    }

    @SneakyThrows
    @Test
    public void simpleReturnTrueTest() {
        var program = "return true;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal.getType()).isEqualTo(Type.BOOLEAN);
        assertThat(returnVal).isInstanceOf(BooleanElement.class);
        assertThat(((BooleanElement) returnVal).getValue()).isTrue();
    }

    @SneakyThrows
    @Test
    public void simpleReturnNegFalseTest() {
        var program = "return !False;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal.getType()).isEqualTo(Type.BOOLEAN);
        assertThat(returnVal).isInstanceOf(BooleanElement.class);
        assertThat(((BooleanElement) returnVal).getValue()).isTrue();
    }

    @SneakyThrows
    @Test
    public void simpleReturnNegTrueTest() {
        var program = "return !true;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal.getType()).isEqualTo(Type.BOOLEAN);
        assertThat(returnVal).isInstanceOf(BooleanElement.class);
        assertThat(((BooleanElement) returnVal).getValue()).isFalse();
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("mathInfixOperationTestsSource")
    public void mathInfixOperationTests(String op, AbstractElementType n1, AbstractElementType n2, AbstractElementType erg, Type ergTyp) {


        var program = String.format("return %s%s%s;", n1.toString(), op, n2.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal.getType()).isEqualTo(ergTyp);

        if (ergTyp == Type.INTEGER_NUMBER) {
            int value = (Integer) returnVal.getValue();
            Integer expect = (Integer) (erg.getValue());
            assertThat(value).isCloseTo(expect, Percentage.withPercentage(0.1));
        } else if (ergTyp == Type.DOUBLE) {
            Double value = (Double) returnVal.getValue();
            Double expect = (Double) (erg.getValue());
            assertThat(value).isCloseTo(expect, Percentage.withPercentage(0.1));
        }

    }

    @ParameterizedTest
    @MethodSource("mathInfixOperationTestsInvalidSource")
    public void mathInfixOperationInvalidTests(String op, AbstractElementType n1, AbstractElementType n2) {

        var program = String.format("return %s%s%s;", n1.toString(), op, n2.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        assertThatThrownBy(() -> virtualMachine.run(null))
                .isInstanceOf(VirtualMachineException.class)
                .hasMessageContaining(
                        String.format("on %s and %s!", n1.getType(), n2.getType())
                );
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("mathInfixOperationArrayTestsSource")
    public void mathInfixOperationArrayTests(String op, AbstractElementType<?> n1, AbstractElementType<?> n2, ArrayElement erg) {

        var program = String.format("return %s%s%s;", n1.toString(), op, n2.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnedVal = virtualMachine.run(null);


        assertThat(returnedVal).isInstanceOf(ArrayElement.class);

        ArrayElement returnedArray = (ArrayElement) returnedVal;
        assertThat(returnedArray.getValue())
                .hasSize(erg.getValue().size());

        for (int i = 0; i < returnedArray.getValue().size(); i++) {
            Number value = null;
            Number expect = null;

            if (erg.get(i).getType() == Type.INTEGER_NUMBER) {
                expect = (Integer) (erg.get(i).getValue());
            } else if (erg.get(i).getType() == Type.DOUBLE) {
                expect = (Double) (erg.get(i).getValue());
            }

            if (returnedArray.get(i).getType() == Type.INTEGER_NUMBER) {
                value = (Integer) returnedArray.get(i).getValue();
                assertThat((Integer) value).isCloseTo(Objects.requireNonNull(expect).intValue(), Percentage.withPercentage(0.1));
            } else if (returnedArray.get(i).getType() == Type.DOUBLE) {
                value = (Double) returnedArray.get(i).getValue();
                assertThat((Double) value).isCloseTo(Objects.requireNonNull(expect).doubleValue(), Percentage.withPercentage(0.1));
            }
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("negateUnarySource")
    public void negateUnaryTest(AbstractElementType<?> ele, AbstractElementType<?> erg) {

        var program = String.format("return !%s;", ele.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnedVal = virtualMachine.run(null);

        assertThat(returnedVal.getType()).isEqualTo(erg.getType());
        assertThat(returnedVal.getValue()).isEqualTo(erg.getValue());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("stringConcatenationSource")
    public void stringConcatenationTest(AbstractElementType<?> e1, AbstractElementType<?> e2, StringElement erg) {

        var program = String.format("return %s++%s;", e1.toString(), e2.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnedVal = virtualMachine.run(null);

        assertThat(returnedVal.getType()).isEqualTo(Type.STRING);

        StringElement castedReturnVal = (StringElement) returnedVal;

        assertThat(castedReturnVal.getValue()).isEqualTo(erg.getValue());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("accessStringOrArraySource")
    public void accessStringOrArrayTest(AbstractElementType<?> elem, int index, AbstractElementType<?> erg) {

        var program = String.format("var testVar := %s; return testVar[%s];", elem.toString(), index + "");

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnedVal = virtualMachine.run(null);

        assertThat(returnedVal).isEqualTo(erg);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("accessStringOrArraySource")
    public void accessStringOrArrayMoreComplexTest(AbstractElementType<?> elem, int index, AbstractElementType<?> erg) {

        var program = String.format("var testVar := %s; return testVar[((%s * 1) + 1) - 1];", elem.toString(), index + "");

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnedVal = virtualMachine.run(null);

        assertThat(returnedVal).isEqualTo(erg);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidIndexAccessStringOrArraySource")
    public void invalidIndexAccessStringOrArrayTest(AbstractElementType<?> elem, AbstractElementType<?> index) {

        var program = String.format("var testVar := %s; return testVar[%s];", elem.toString(), index + "");

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);

        assertThatThrownBy(() -> virtualMachine.run(null))
                .isInstanceOf(VirtualMachineException.class)
                .hasMessageContaining(String.format("You can not use a %s to access an element of", index.getType()));

    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidTypeAccessIndexSource")
    public void invalidTypeAccessIndexTest(AbstractElementType<?> elem, boolean shouldThrow) {
        var program = String.format("var testVar := %s; return testVar[0];", elem.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);

        if (shouldThrow) {
            assertThatThrownBy(() -> virtualMachine.run(null))
                    .isInstanceOf(VirtualMachineException.class)
                    .hasMessageContaining(String.format("You can not use index accessing on an element of type %s", elem.getType()));
        } else {
            assertThat(virtualMachine.run(null)).isNotNull();
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("accessVariableSource")
    public void accessVariableTest(AbstractElementType<?> elem) {

        var program = String.format("var dontCar := 1;" +
                "var testVar := %s;" +
                "var dontCar2 := 2;" +
                "var dontCar3 := 3;" +
                "return testVar;", elem.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal).isEqualTo(elem);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("validLogicOperationSource")
    public void validLogicOperationTest(String op, BooleanElement b1, BooleanElement b2, boolean erg) {

        var program = String.format("return %s%s%s;", b1.toString(), op, b2.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnedVal = virtualMachine.run(null);

        if (erg) {
            assertThat(returnedVal).isEqualTo(Booleans.TRUE);
        } else {
            assertThat(returnedVal).isEqualTo(Booleans.FALSE);
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidLogicOperationSource")
    public void invalidLogicOperationTest(String op, AbstractElementType<?> e1, AbstractElementType<?> e2, boolean shouldThrow) {
        var program = String.format("return %s%s%s;", e1.toString(), op, e2.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);

        String opName = (op.equalsIgnoreCase("&&")) ? "and" : "or";

        if (shouldThrow) {
            assertThatThrownBy(() -> virtualMachine.run(null))
                    .isInstanceOf(VirtualMachineException.class)
                    .hasMessageContaining(String.format("The logic `%s` operation only works with booleans! You want to perform the `%s` operation on a %s and %s", opName, opName, e1.getType(), e2.getType()));
        } else {
            AbstractElementType<?> returnedVal = virtualMachine.run(null);
            assertThat(returnedVal).isNotNull();
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("equalsSource")
    public void equalsTest(AbstractElementType<?> e1, AbstractElementType<?> e2, boolean erg) {

        var program = String.format("return %s==%s;", e1.toString(), e2.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnedVal = virtualMachine.run(null);

        if (erg) {
            assertThat(returnedVal).isEqualTo(Booleans.TRUE);
        } else {
            assertThat(returnedVal).isEqualTo(Booleans.FALSE);
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("notequalsSource")
    public void notequalsTest(AbstractElementType<?> e1, AbstractElementType<?> e2, boolean erg) {

        var program = String.format("return %s!=%s;", e1.toString(), e2.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnedVal = virtualMachine.run(null);

        if (erg) {
            assertThat(returnedVal).isEqualTo(Booleans.TRUE);
        } else {
            assertThat(returnedVal).isEqualTo(Booleans.FALSE);
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("validCompareSource")
    public void validCompareTest(String op, AbstractElementType<?> e1, AbstractElementType<?> e2, BooleanElement erg) {

        var program = String.format("return %s%s%s;", e1.toString(), op, e2.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnedVal = virtualMachine.run(null);

        assertThat(returnedVal).isEqualTo(erg);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidCompareSource")
    public void invalidCompareTest(String op, AbstractElementType<?> e1, AbstractElementType<?> e2, boolean shouldThrow) {

        var program = String.format("return %s%s%s;", e1.toString(), op, e2.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);

        if (shouldThrow) {
            assertThatThrownBy(() -> virtualMachine.run(null))
                    .isInstanceOf(VirtualMachineException.class)
                    .hasMessageContaining(String.format("The `%s` operation only works with numbers or integers. You tried to use it on %s and %s", op, e1.getType(), e2.getType()));
        } else {
            AbstractElementType<?> returnedVal = virtualMachine.run(null);
            assertThat(returnedVal).isNotNull();
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("validExternalParamSource")
    public void validExternalParamTest(Map<String, String> externalParams, String name, StringElement erg) {
        var program = String.format("return #%s;", name);

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(externalParams);

        assertThat(returnVal).isEqualTo(erg);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidExternalParamSource")
    public void invalidExternalParamTest(Map<String, String> externalParams, String name, boolean shouldThrow) {
        var program = String.format("return #%s;", name);

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);

        if (shouldThrow) {
            assertThatThrownBy(() -> virtualMachine.run(externalParams))
                    .isInstanceOf(VirtualMachineException.class)
                    .hasMessageContaining(String.format("The external parameter `%s` you want to access does not exist!", name));
        } else {
            AbstractElementType<?> returnVal = virtualMachine.run(externalParams);
            assertThat(returnVal).isNotNull();
        }
    }

    @Test
    @SneakyThrows
    public void reassignVariableTest() {

        var program = "var x:= 10;" +
                "x:= \"Hey!\";" +
                "return x;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal).isInstanceOf(StringElement.class);

        StringElement castedReturnVal = (StringElement) returnVal;
        assertThat(castedReturnVal.getValue()).isEqualTo("Hey!");
    }

    @Test
    @SneakyThrows
    public void simpleIfTest() {
        var program = "var x := 10.0;" +
                "if( (1<2) && (2==(1+1)) ) {" +
                "x := 2;" +
                "}" +
                "return x;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal).isInstanceOf(IntegerElement.class);

        IntegerElement castedReturnVal = (IntegerElement) returnVal;
        assertThat(castedReturnVal.getValue()).isEqualTo(2);
    }

    @Test
    @SneakyThrows
    public void simpleIfNotTriggeredTest() {
        var program = "var x := 10.0;" +
                "if( (1<2) && (2!=(1+1)) ) {" +
                "x := 2;" +
                "}" +
                "return x;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal).isInstanceOf(DoubleElement.class);

        DoubleElement castedReturnVal = (DoubleElement) returnVal;
        assertThat(castedReturnVal.getValue()).isEqualTo(10.0);
    }

    @ParameterizedTest
    @MethodSource("simpleInvalidIfSource")
    @SneakyThrows
    public void simpleInvalidIfTest(AbstractElementType<?> e1, boolean shouldThrow) {
        var program = String.format("var x := 10.0;" +
                "if( %s ) {" +
                "x := 2;" +
                "}" +
                "return x;", e1.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);

        if (shouldThrow) {
            assertThatThrownBy(() -> virtualMachine.run(null))
                    .isInstanceOf(VirtualMachineException.class)
                    .hasMessageContaining(String.format("Conditions has to be of type boolean not %s!", e1.getType()));
        } else {
            assertThat(virtualMachine.run(null)).isNotNull();
        }
    }


    @Test
    @SneakyThrows
    public void simpleIfElseTest() {
        var program = "var x := 10.0;" +
                "if( (1<2) && (2==(1+1)) ) {" +
                "x := 2;" +
                "}else {" +
                "x := 99;" +
                "}" +
                "return x;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal).isInstanceOf(IntegerElement.class);

        IntegerElement castedReturnVal = (IntegerElement) returnVal;
        assertThat(castedReturnVal.getValue()).isEqualTo(2);
    }

    @Test
    @SneakyThrows
    public void simpleIfElseNotTriggeredTest() {
        var program = "var x := 10.0;" +
                "if( (1<2) && (2!=(1+1)) ) {" +
                "x := 2;" +
                "}else {" +
                "x := 99.9;" +
                "}" +
                "return x;";

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

        assertThat(returnVal).isInstanceOf(DoubleElement.class);

        DoubleElement castedReturnVal = (DoubleElement) returnVal;
        assertThat(castedReturnVal.getValue()).isEqualTo(99.9);
    }

    @ParameterizedTest
    @MethodSource("simpleInvalidIfSource")
    @SneakyThrows
    public void simpleInvalidIfElseTest(AbstractElementType<?> e1, boolean shouldThrow) {
        var program = String.format("var x := 10.0;" +
                "if( %s ) {" +
                "x := 2;" +
                "}else {" +
                "x := 99;" +
                "}" +
                "return x;", e1.toString());

        // lexing
        BoLexer boLexer = new BoLexer(CharStreams.fromString(program));
        CommonTokenStream tokens = new CommonTokenStream(boLexer);

        // parsing
        BoParser boParser = new BoParser(tokens);
        BoParser.BoContext bo = boParser.bo();

        // AST generator
        BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
        BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

        VirtualMachine virtualMachine = getVirtualMachine(head);

        if (shouldThrow) {
            assertThatThrownBy(() -> virtualMachine.run(null))
                    .isInstanceOf(VirtualMachineException.class)
                    .hasMessageContaining(String.format("Conditions has to be of type boolean not %s!", e1.getType()));
        } else {
            assertThat(virtualMachine.run(null)).isNotNull();
        }
    }


}
