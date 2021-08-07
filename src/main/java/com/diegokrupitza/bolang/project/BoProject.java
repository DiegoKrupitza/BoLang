package com.diegokrupitza.bolang.project;

import com.diegokrupitza.bolang.project.exceptions.BoProjectException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.FileNotFoundException;
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

    public static final String BO_PROJECT_JSON = "./BoProject.json";
    private static final String BO_MODULES_DIR = "./bo_modules/";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    @Setter(AccessLevel.NONE)
    private Path projectBase;

    private BoProjectPojo projectPojo;

    @Getter
    private String rawJsonContent;

    public BoProject(Path projectBase) throws BoProjectException {
        this.projectBase = projectBase;
        readBoProjectJson();
    }

    private void readBoProjectJson() throws BoProjectException {
        try {
            Path jsonPath = projectBase.resolve(Path.of(BO_PROJECT_JSON)).normalize();
            rawJsonContent = Files.readString(jsonPath);

            this.projectPojo = objectMapper.readValue(rawJsonContent, BoProjectPojo.class);
        } catch (FileNotFoundException e) {
            throw new BoProjectException("File not found: " + e.getMessage());
        } catch (IOException e) {
            throw new BoProjectException(e.getMessage());
        }
    }

    public Map<String, String> getProjectParams() {
        return this.projectPojo.getParams();
    }

    public Path getMainPath() {
        return projectBase.resolve(projectPojo.getMain()).normalize();
    }

    public boolean isExternalModule(String nameOfModule) {
        String moduleFileName = projectPojo.getModules().getOrDefault(nameOfModule, "");
        return moduleFileName.startsWith("@");
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
