package com.diegokrupitza.bolang;

import com.diegokrupitza.bolang.project.BoProject;
import com.diegokrupitza.bolang.project.BoProjectValidator;
import com.diegokrupitza.bolang.util.CmdUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 24.07.21
 */
public class BoLang {

    public static final String BO_LANG_NAME = "BoLang";

    private static final Properties BoLangProperties = new Properties();
    private static final String propertiesFileName = "BoLang.properties";

    private static BoService boService;

    public static void main(String[] args) {

        // props
        try {
            BoLangProperties.load(BoLang.class.getClassLoader().getResourceAsStream(propertiesFileName));
        } catch (IOException e) {
            CmdUtilities.error("Could not load BoLang Properties");
        }

        HelpFormatter hf = new HelpFormatter();

        // defining the option for the args processing
        Options options = new Options();
        options.addOption("f", false, "Enable function mode (allows self defined functions)");
        options.addOption("h", "help", false, "Prints this help information");
        options.addOption("v", "version", false, "Display the current BoLang version");
        options.addOption("p", "params", true, "Allows you to provide params in a JSON format to the program");

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);


            // generate new boService based on options etc
            BoService.Builder boServiceBuilder = BoService.builder()
                    .functions(cmd.hasOption('f'));

            if (cmd.hasOption('h')) {
                hf.printHelp(BO_LANG_NAME, options, true);
                return;
            }

            if (cmd.hasOption('v')) {
                System.out.println("Version: " + BoLangProperties.getProperty("version"));
                return;
            }

            List<String> boLangFiles = cmd.getArgList();
            if (boLangFiles.size() != 1) {
                //TODO: better exception
                throw new RuntimeException("You can only run a single BoLang program at once!");
            }

            String fileName = boLangFiles.get(0);
            Path boLangCodeFile = Paths.get(fileName);

            if (Files.isDirectory(boLangCodeFile)) {
                // we are dealing with a BoLang project
                BoProject boProject = new BoProject(boLangCodeFile);
                BoProjectValidator.validate(boProject);

                boServiceBuilder = boServiceBuilder.project(boProject);

                if (!boProject.getProjectParams().isEmpty()) {
                    // adding the project defined params
                    boServiceBuilder.addParams(boProject.getProjectParams());
                }

                // setting the main as new `boLangCodeFile`
                boLangCodeFile = boProject.getMainPath();
            }


            // params are the last thing we will do since last specified
            // params in the command line will override any other previous present ones
            if (cmd.hasOption('p')) {
                String paramsAsString = cmd.getOptionValue('p');

                // replace all single quotes with double quotes
                // since single is not valid JSON
                paramsAsString = paramsAsString.replaceAll("'", "\"");

                Map<String, String> params = new ObjectMapper().readValue(paramsAsString, Map.class);

                boServiceBuilder = boServiceBuilder.addParams(params);
            }

            boService = boServiceBuilder.build();

            String boLangFileContent = Files.readString(boLangCodeFile);

            // finaly run!
            boService.run(boLangFileContent);

        } catch (IOException e) {
            if (e instanceof NoSuchFileException) {
                CmdUtilities.error("Could not find the file: " + e.getMessage());
            }
        } catch (Exception e) {
            //TODO better exception handling in the future
            CmdUtilities.error(e.getMessage());
            if (e instanceof ParseException) {
                hf.printHelp(BO_LANG_NAME, options, true);
            }
        }
    }
}
