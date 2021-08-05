package com.diegokrupitza.bolang.project;

import com.diegokrupitza.bolang.project.exceptions.BoProjectException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 04.08.21
 */
public class BoProject {

    private static final String BO_MODULES_DIR = "./bo_modules/";
    public static final String BO_PROJECT_JSON = "./BoProject.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        try {
            Path jsonPath = projectBase.resolve(Path.of(BO_PROJECT_JSON)).normalize();
            this.projectPojo = objectMapper.readValue(Files.readString(jsonPath), BoProjectPojo.class);
        } catch (IOException e) {
            throw new BoProjectException(e.getMessage());
        }
    }

    public void checkProjectStructure() throws BoProjectException {
        //TODO check if the BoProject.json is in valid format
        // and all the files exists that a defined
    }

    public Map<String, String> getProjectParams() {
        return this.projectPojo.getParams();
    }

    public Path getMainPath() {
        return projectBase.resolve(projectPojo.getMain()).normalize();
    }

    public Path getModulePath(String nameOfModule) throws BoProjectException {
        if (!projectPojo.getModules().containsKey(nameOfModule)) {
            throw new BoProjectException(String.format("Module with the name `%s` was not defined in the project modules section!", nameOfModule));
        }


        String moduleFileName = projectPojo.getModules().get(nameOfModule);

        if (moduleFileName.startsWith("@")) {
            // we are dealing with a bo_modules dependency
            String moduleDirName = moduleFileName.substring(1);

            Path moduleProjectBase = projectBase
                    .resolve(BO_MODULES_DIR)
                    .resolve(moduleDirName)
                    .normalize();

            BoProject moduleBoProject = new BoProject(moduleProjectBase);
            return moduleBoProject.getMainPath();
        }

        return projectBase.resolve(Path.of(moduleFileName)).normalize();
    }
}
