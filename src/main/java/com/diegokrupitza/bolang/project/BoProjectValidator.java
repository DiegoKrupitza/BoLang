package com.diegokrupitza.bolang.project;

import com.diegokrupitza.bolang.project.exceptions.BoProjectValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Objects;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 05.08.21
 */
public class BoProjectValidator {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BoProjectSchema = "BoProject.schema.json";

    public static void validate(BoProject boProject) throws BoProjectValidationException {
        validateJson(boProject.getRawJsonContent());
        validateExternalBoModules(boProject);
    }

    private static void validateExternalBoModules(BoProject boProject) {
        //TODO: check if all the modules in `bo_modules` are valid
    }

    protected static void validateJson(String rawJson) throws BoProjectValidationException {
        try {
            JSONObject jsonSchema = new JSONObject(
                    new JSONTokener(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(BoProjectSchema))));

            JSONObject jsonSubject = new JSONObject(new JSONTokener(rawJson));

            Schema schema = SchemaLoader.load(jsonSchema);

            schema.validate(jsonSubject);
        } catch (Exception e) {
            throw new BoProjectValidationException("BoProjectJson: " + e.getMessage());
        }
    }

}
