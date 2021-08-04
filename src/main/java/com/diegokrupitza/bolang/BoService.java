package com.diegokrupitza.bolang;

import com.diegokrupitza.bolang.syntaxtree.BuildAstVisitor;
import com.diegokrupitza.bolang.syntaxtree.nodes.BoNode;
import com.diegokrupitza.bolang.syntaxtree.nodes.FunctionNode;
import com.diegokrupitza.bolang.vm.VirtualMachine;
import com.diegokrupitza.bolang.vm.VirtualMachineException;
import com.diegokrupitza.bolang.vm.types.AbstractElementType;
import com.diegokrupitza.pdfgenerator.BoLexer;
import com.diegokrupitza.pdfgenerator.BoParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 04.08.21
 */
public class BoService {

    private final boolean functionsAllowed;

    public BoService(Builder builder) {
        this.functionsAllowed = builder.functionsAllowed;
    }

    public static BoService.Builder builder() {
        return new BoService.Builder();
    }

    private static VirtualMachine getVirtualMachine(BoNode head) throws VirtualMachineException {
        return new VirtualMachine(head);
    }

    public void run(String boLangFileContent) throws VirtualMachineException {
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

        if (!this.functionsAllowed && containsFunctions(head)) {
            throw new VirtualMachineException("You are currently in `non function` mode! " +
                    "This means you are not allowed to have self defined functions in your code! " +
                    "If you want to change that please use the flag `-f`");
        }

        VirtualMachine virtualMachine = getVirtualMachine(head);
        AbstractElementType<?> returnVal = virtualMachine.run(null);

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

        private boolean functionsAllowed = false;

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

    }
}
