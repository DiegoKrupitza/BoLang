package com.diegokrupitza.bolang.project;

import com.diegokrupitza.bolang.project.exceptions.BoProjectValidationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 05.08.21
 */
public class BoProjectValidatorTest {

    @SneakyThrows
    @Test
    void validJsonTest() {
        var json = "{\n" +
                "  \"name\": \"Testcase Project\",\n" +
                "  \"description\": \"A testcase description you can choose\",\n" +
                "  \"main\": \"Main.bo\",\n" +
                "  \"modules\": {\n" +
                "    \"module1\": \"Module1.bo\",\n" +
                "    \"module2\": \"Module2.bo\"\n" +
                "  },\n" +
                "  \"params\": {\n" +
                "    \"param1\": \"1\",\n" +
                "    \"param2\": \"2\"\n" +
                "  }\n" +
                "}\n";

        BoProjectValidator.validateJson(json);
    }

    @SneakyThrows
    @Test
    void invalidJsonTest() {
        var json = "{\n" +
                "  \"description\": \"A testcase description you can choose\",\n" +
                "  \"main\": \"Main.bo\",\n" +
                "  \"modules\": {\n" +
                "    \"module1\": \"Module1.bo\",\n" +
                "    \"module2\": \"Module2.bo\"\n" +
                "  },\n" +
                "  \"params\": {\n" +
                "    \"param1\": \"1\",\n" +
                "    \"param2\": \"2\"\n" +
                "  }\n" +
                "}\n";

        assertThatThrownBy(() -> BoProjectValidator.validateJson(json))
                .isInstanceOf(BoProjectValidationException.class)
                .hasMessageContaining("name");
    }

    @SneakyThrows
    @Test
    void secondInvalidJsonTest() {
        var json = "{\n" +
                "  \"description\": \"A testcase description you can choose\",\n" +
                "  \"main\": \"Main.bo\",\n" +
                "  \"modules\": {\n" +
                "    \"module1\": \"Module1.bo\",\n" +
                "    \"module1\": \"Module2.bo\"\n" +
                "  },\n" +
                "  \"params\": {\n" +
                "    \"param1\": \"1\",\n" +
                "    \"param2\": \"2\"\n" +
                "  }\n" +
                "}\n";

        assertThatThrownBy(() -> BoProjectValidator.validateJson(json))
                .isInstanceOf(BoProjectValidationException.class)
                .hasMessageContaining("Duplicate key");
    }
}
