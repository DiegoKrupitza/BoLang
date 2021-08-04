package com.diegokrupitza.bolang.project;

import com.diegokrupitza.bolang.project.exceptions.BoProjectException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 04.08.21
 */
@Data
@NoArgsConstructor
public class BoProject {

    @Getter
    @Setter(AccessLevel.NONE)
    private Path projectBase;

    private BoProjectPojo projectPojo;

    public BoProject(Path projectBase) throws BoProjectException {
        this.projectBase = projectBase;
        checkProjectStructure();
        readBoProjectJson();
    }

    private void readBoProjectJson() throws BoProjectException {
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            Path jsonPath = projectBase.resolve(Path.of("./BoProject.json")).normalize();
            this.projectPojo = objectMapper.readValue(Files.readString(jsonPath), BoProjectPojo.class);
        } catch (IOException e) {
            throw new BoProjectException(e.getMessage());
        }
    }

    public void checkProjectStructure() throws BoProjectException {
        //TODO check if the BoProject.json is in valid format
        // and all the files exists that a defined
    }

    public Path getMain() {
        return projectBase.resolve(projectPojo.getMain()).normalize();
    }

    public Path getModulePath(String nameOfModule) {
        return projectBase.resolve(Path.of(projectPojo.getModules().get(nameOfModule))).normalize();
    }
}
