package com.diegokrupitza.bolang.project;

import com.diegokrupitza.bolang.project.exceptions.BoProjectException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 04.08.21
 */
public class BoProjectTest {

    @SneakyThrows
    @Test
    void simpleObjectCreationTest() {
        Path projectRoot = Paths.get("src", "test", "resources", "boTestProject");
        BoProject boProject = new BoProject(projectRoot);

        assertThat(boProject.getMainPath().toString()).contains("/Main.bo");
    }

    @SneakyThrows
    @Test
    void validModuleTest() {
        Path projectRoot = Paths.get("src", "test", "resources", "boTestProject");
        BoProject boProject = new BoProject(projectRoot);

        assertThat(boProject.getModulePath("module1").toString()).contains("/Module1.bo");
        assertThat(boProject.getModulePath("module2").toString()).contains("/Module2.bo");
    }

    @SneakyThrows
    @Test
    void invalidModuleTest() {
        Path projectRoot = Paths.get("src", "test", "resources", "boTestProject");
        BoProject boProject = new BoProject(projectRoot);

        assertThatThrownBy(() -> boProject.getModulePath("invalidModule1"))
                .isInstanceOf(BoProjectException.class)
                .hasMessageContaining("Module with the name `invalidModule1` was not defined in the project modules section!");

    }
}
