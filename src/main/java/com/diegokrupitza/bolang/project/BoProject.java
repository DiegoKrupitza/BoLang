package com.diegokrupitza.bolang.project;

import com.diegokrupitza.bolang.project.exceptions.BoProjectException;
import lombok.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 04.08.21
 */
@Data
@NoArgsConstructor
public class BoProject {

    // modulename, path
    private Map<String, String> modules = new HashMap<>();

    @Getter
    @Setter(AccessLevel.NONE)
    private Path projectBase;

    public BoProject(Path projectBase) {
        this.projectBase = projectBase;
        readBoProjectJson();
    }

    private void readBoProjectJson() {
        //TODO:

        // reading the modules
        modules.put("module1", "./Module1.bo");
        modules.put("module2", "./Module2.bo");
    }

    public void checkProjectStructure() throws BoProjectException {
        //TODO check if the BoProject.json is in valid format
        // and all the files exists that a defined
    }

    public Path getMain() {
        //TODO: read param from json
        return projectBase.resolve("Main.bo");
    }

    public Path getModulePath(String nameOfModule) {
        return projectBase.resolve(Path.of(modules.get(nameOfModule))).normalize();
    }
}
