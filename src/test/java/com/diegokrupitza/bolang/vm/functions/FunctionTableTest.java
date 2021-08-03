package com.diegokrupitza.bolang.vm.functions;

import com.diegokrupitza.bolang.syntaxtree.nodes.FunctionNode;
import com.diegokrupitza.bolang.vm.functions.exceptions.FunctionTableException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 03.08.21
 */
public class FunctionTableTest {

    @SneakyThrows
    @Test
    void initFunctionTableOverConstructor() {
        List<FunctionNode> functionNodes = List.of(new FunctionNode("testFunc", List.of("a", "b"), List.of()), new FunctionNode("testFunc", List.of("a"), List.of()));
        FunctionTable functionTable = new FunctionTable(functionNodes);
    }

    @SneakyThrows
    @Test
    void addFunctionTest() {
        FunctionTable functionTable = new FunctionTable(List.of());

        FunctionNode addFunction = new FunctionNode("testFunc", List.of("a", "b"), List.of());
        functionTable.add(addFunction);
    }

    @SneakyThrows
    @Test
    void addDuplicatedFunctionInvalidTest() {
        FunctionTable functionTable = new FunctionTable(List.of());

        FunctionNode addFunction = new FunctionNode("testFunc", List.of("a", "b"), List.of());
        FunctionNode duplicatedFunction = new FunctionNode("testFunc", List.of("a", "b"), List.of());

        functionTable.add(addFunction);

        assertThatThrownBy(() -> functionTable.add(duplicatedFunction))
                .hasMessageContaining("The function with the name testFunc and the params [a, b] is already defined! Functions have to be unique in the combination of name and params (count and name)");
    }

    @SneakyThrows
    @Test
    void addNotDuplicatedFunctionTest() {
        FunctionTable functionTable = new FunctionTable(List.of());

        FunctionNode addFunction = new FunctionNode("testFunc", List.of("a", "b"), List.of());
        FunctionNode notDuplicatedFunction = new FunctionNode("testFunc", List.of("a"), List.of());

        functionTable.add(addFunction);
        functionTable.add(notDuplicatedFunction);
    }

    @SneakyThrows
    @Test
    void getFunctionTest() {
        FunctionTable functionTable = new FunctionTable(List.of());

        FunctionNode addFunction = new FunctionNode("testFunc", List.of("a", "b"), List.of());
        FunctionNode notDuplicatedFunction = new FunctionNode("testFunc", List.of("a"), List.of());

        functionTable.add(addFunction);
        functionTable.add(notDuplicatedFunction);

        FunctionNode firstGet = functionTable.get("testFunc", 2);
        assertThat(firstGet).isEqualTo(addFunction);

        FunctionNode secondGet = functionTable.get("testFunc", 1);
        assertThat(secondGet).isEqualTo(notDuplicatedFunction);
    }

    @SneakyThrows
    @Test
    void getFunctionInvalidTest() {
        FunctionTable functionTable = new FunctionTable(List.of());

        FunctionNode addFunction = new FunctionNode("testFunc", List.of("a", "b"), List.of());
        FunctionNode notDuplicatedFunction = new FunctionNode("testFunc", List.of("a"), List.of());

        functionTable.add(addFunction);
        functionTable.add(notDuplicatedFunction);

        assertThatThrownBy(() -> functionTable.get("testFunc", 3))
                .isInstanceOf(FunctionTableException.class)
                .hasMessageContaining("Cannot find the function testFunc with 3 parameters");

        assertThatThrownBy(() -> functionTable.get("wrongName", 3))
                .isInstanceOf(FunctionTableException.class)
                .hasMessageContaining("Cannot find the function wrongName with 3 parameters");
    }
}
