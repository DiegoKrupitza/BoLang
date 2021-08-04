package com.diegokrupitza.bolang;

import com.diegokrupitza.bolang.util.CmdUtilities;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

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

    private static BoService boService;

    public static void main(String[] args) {
        try {
            // defining the option for the args processing
            Options options = new Options();
            options.addOption("f", false, "Enable function mode (allows self defined functions)");


            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

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
        }

    }
}
