package com.diegokrupitza.bolang;

import com.diegokrupitza.bolang.util.CmdUtilities;
import org.apache.commons.cli.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 24.07.21
 */
public class BoLang {

    public static final String BO_LANG_NAME = "BoLang";

    private static BoService boService;

    public static void main(String[] args) {

        HelpFormatter hf = new HelpFormatter();

        // defining the option for the args processing
        Options options = new Options();
        options.addOption("f", false, "Enable function mode (allows self defined functions)");
        options.addOption("h", "help", false, "Prints this help information");

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption('h')) {
                hf.printHelp(BO_LANG_NAME, options, true);
                return;
            }

            List<String> boLangFiles = cmd.getArgList();
            if (boLangFiles.size() != 1) {
                //TODO: better exception
                throw new RuntimeException("You can only run a single BoLang program at once!");
            }

            String fileName = boLangFiles.get(0);
            Path boLangCodeFile = Paths.get(fileName);

            String boLangFileContent = Files.readString(boLangCodeFile);

            // generate new boService based on options etc
            boService = BoService.builder()
                    .functions(cmd.hasOption('f'))
                    .build();

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
