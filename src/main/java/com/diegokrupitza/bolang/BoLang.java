package com.diegokrupitza.bolang;

import com.diegokrupitza.bolang.syntaxtree.BuildAstVisitor;
import com.diegokrupitza.bolang.syntaxtree.nodes.BoNode;
import com.diegokrupitza.bolang.vm.VirtualMachine;
import com.diegokrupitza.bolang.vm.VirtualMachineException;
import com.diegokrupitza.bolang.vm.types.AbstractElementType;
import com.diegokrupitza.pdfgenerator.BoLexer;
import com.diegokrupitza.pdfgenerator.BoParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
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

    public static void main(String[] args) {
        try {
            // defining the option for the args processing
            Options options = new Options();

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

            // lexing
            BoLexer boLexer = new BoLexer(CharStreams.fromString(boLangFileContent));
            boLexer.removeErrorListeners();
            boLexer.addErrorListener(ThrowingErrorListener.INSTANCE);


            CommonTokenStream tokens = new CommonTokenStream(boLexer);

            // parsing
            BoParser boParser = new BoParser(tokens);

            boParser.removeErrorListeners();
            boParser.addErrorListener(ThrowingErrorListener.INSTANCE);

            BoParser.BoContext bo = boParser.bo();

            // AST generator
            BuildAstVisitor buildAstVisitor = new BuildAstVisitor();
            BoNode head = (BoNode) buildAstVisitor.visitBo(bo);

            VirtualMachine virtualMachine = getVirtualMachine(head);
            AbstractElementType<?> returnVal = virtualMachine.run(null);

            if (returnVal != null) {
                System.out.println(returnVal.toString());
            }

        } catch (Exception e) {
            //TODO better exception handling in the future
            e.printStackTrace();
        }

    }

    private static VirtualMachine getVirtualMachine(BoNode head) throws VirtualMachineException {
        return new VirtualMachine(head);
    }
}
