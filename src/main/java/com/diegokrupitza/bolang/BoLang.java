package com.diegokrupitza.bolang;

import com.diegokrupitza.bolang.project.BoProject;
import com.diegokrupitza.bolang.util.CmdUtilities;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

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

            // generate new boService based on options etc
            BoService.Builder boServiceBuilder = BoService.builder()
                    .functions(cmd.hasOption('f'));

            if (Files.isDirectory(boLangCodeFile)) {
                // we are dealing with a BoLang project
                BoProject boProject = new BoProject(boLangCodeFile);
                boServiceBuilder = boServiceBuilder.project(boProject);

                // setting the main as new `boLangCodeFile`
                boLangCodeFile = boProject.getMainPath();
            }

            boService = boServiceBuilder.build();

            String boLangFileContent = Files.readString(boLangCodeFile);

            // finaly run!
            boService.run(boLangFileContent);

        } catch (Exception e) {
            //TODO better exception handling in the future
            CmdUtilities.error(e.getMessage());
            if (e instanceof ParseException) {
                hf.printHelp(BO_LANG_NAME, options, true);
            }
        }
    }
}
