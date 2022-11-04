package com.diegokrupitza.bolang;

import com.diegokrupitza.bolang.project.BoProject;
import com.diegokrupitza.bolang.syntaxtree.BuildAstVisitor;
import com.diegokrupitza.bolang.syntaxtree.nodes.BoNode;
import com.diegokrupitza.bolang.syntaxtree.nodes.FunctionNode;
import com.diegokrupitza.bolang.vm.ModulesImporter;
import com.diegokrupitza.bolang.vm.VirtualMachine;
import com.diegokrupitza.bolang.vm.VirtualMachineException;
import com.diegokrupitza.bolang.vm.types.AbstractElementType;
import com.diegokrupitza.pdfgenerator.BoLexer;
import com.diegokrupitza.pdfgenerator.BoParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 04.08.21
 */
public class BoService {

    private final boolean functionsAllowed;
    private final BoProject boProject;
    private final Map<String, String> externalParams;

    public BoService(Builder builder) {
        this.functionsAllowed = builder.functionsAllowed;
        this.boProject = builder.project;
        this.externalParams = builder.params;
    }

    public static BoService.Builder builder() {
        return new BoService.Builder();
    }

    private static VirtualMachine getVirtualMachine(BoNode head) throws VirtualMachineException {
        return new VirtualMachine(head);
    }

    public static BoNode parseContent(String boLangFileContent) {
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
        return (BoNode) buildAstVisitor.visitBo(bo);
    }

    public void run(String boLangFileContent) throws VirtualMachineException {
        BoNode head = parseContent(boLangFileContent);

        if (!this.functionsAllowed && containsFunctions(head)) {
            throw new VirtualMachineException("You are currently in `non function` mode! " +
                    "This means you are not allowed to have self defined functions in your code! " +
                    "If you want to change that please use the flag `-f`");
        }

        if (!this.functionsAllowed && ModulesImporter.containsModuleImport(head)) {
            throw new VirtualMachineException("You are currently in `non function` mode! " +
                    "This means you are not allowed to use modules and imports!" +
                    "If you want to change that please use the flag `-f`");
        }

        VirtualMachine virtualMachine = getVirtualMachine(head);

        // import possible modules
        // extracting the modules and their functions
        Map<String, List<FunctionNode>> moduleFunctionMap = ModulesImporter.importModules(this.boProject, head);
        virtualMachine.addExternalModules(moduleFunctionMap);

        AbstractElementType<?> returnVal = virtualMachine.run(this.externalParams);

        if (returnVal != null) {
            System.out.println(returnVal.toString());
        }
    }

    private boolean containsFunctions(BoNode head) {
        return head.getStats()
                .stream()
                .anyMatch(item -> item instanceof FunctionNode);
    }

    static class Builder {

        private final Map<String, String> params = new HashMap<>();
        private boolean functionsAllowed = false;
        private BoProject project;

        public Builder functions(boolean allowed) {
            this.functionsAllowed = allowed;
            return this;
        }

        public BoService build() {
            BoService boService = new BoService(this);
            validateBoServiceObject(boService);
            return boService;
        }

        private void validateBoServiceObject(BoService boService) {
            //Check here if the build service is still valid
        }

        public Builder project(BoProject boProject) {
            this.project = boProject;
            return this;
        }

        public Builder addParams(Map<String, String> params) {
            this.params.putAll(params);
            return this;
        }
    }
}
