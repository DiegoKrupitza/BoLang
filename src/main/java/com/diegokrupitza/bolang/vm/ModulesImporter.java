package com.diegokrupitza.bolang.vm;

import com.diegokrupitza.bolang.BoService;
import com.diegokrupitza.bolang.project.BoProject;
import com.diegokrupitza.bolang.project.exceptions.BoProjectException;
import com.diegokrupitza.bolang.syntaxtree.nodes.BoNode;
import com.diegokrupitza.bolang.syntaxtree.nodes.FunctionNode;
import com.diegokrupitza.bolang.syntaxtree.nodes.ImportNode;
import com.diegokrupitza.bolang.syntaxtree.nodes.ModuleNode;
import org.apache.commons.collections4.CollectionUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 07.08.21
 */
public class ModulesImporter {

    public static boolean containsModuleImport(BoNode head) {
        return head.getStats()
                .stream()
                .anyMatch(item -> item instanceof ImportNode);
    }

    /**
     * Imports all the used module of the project. Currently it does not check if the modules are really used or not
     *
     * @param boProject the project of where the modules are located
     * @param head      the head of the program we want to import the modules for
     * @return a hashmap including the name of the module with all the function in the module
     * @throws VirtualMachineException in case an error happens such as file not found etc
     */
    public static HashMap<String, List<FunctionNode>> importModules(BoProject boProject, BoNode head) throws VirtualMachineException {
        HashMap<String, List<FunctionNode>> moduleFunctionMap = new HashMap<>();

        if (!containsModuleImport(head)) {
            return moduleFunctionMap;
        }

        try {
            // import functions from other modules
            Set<ImportNode> importModules = head.getStats().stream()
                    .filter(item -> item instanceof ImportNode)
                    .map(item -> ((ImportNode) item))
                    .collect(Collectors.toUnmodifiableSet());

            if (CollectionUtils.isEmpty(importModules)) {
                // no imports
                return moduleFunctionMap;
            }

            // getting the names of the modules we want to import
            Set<String> namesOfModuleToImport = head.getUsedModules();

            // populating the map with the list of all implemented functions in a module
            for (String nameOfModule : namesOfModuleToImport) {
                moduleFunctionMap = performImportForModule(boProject, moduleFunctionMap, nameOfModule);
            }

        } catch (FileNotFoundException e) {
            throw new VirtualMachineException("File not found: " + e.getMessage());
        } catch (BoProjectException | IOException e) {
            throw new VirtualMachineException(e.getMessage());
        }

        return moduleFunctionMap;
    }

    private static HashMap<String, List<FunctionNode>> performImportForModule(BoProject boProject, HashMap<String, List<FunctionNode>> moduleFunctionMap, String nameOfModule) throws BoProjectException, IOException {
        if (moduleFunctionMap.containsKey(nameOfModule)) {
            // we already imported that certain module
            return moduleFunctionMap;
        }

        Path modulePath = boProject.getModulePath(nameOfModule);

        String moduleContent = Files.readString(modulePath);

        // we need to adjust the "this" of the module.
        moduleContent = moduleContent.replaceAll("this\\.", nameOfModule + ".");

        BoNode moduleBoNode = BoService.parseContent(moduleContent);

        assert moduleBoNode.getStats().get(0) instanceof ModuleNode : "This should always be of type ModuleNode!";
        ModuleNode moduleHead = (ModuleNode) moduleBoNode.getStats().get(0);

        // functions in the given module
        List<FunctionNode> functionsOfModule = moduleHead
                .getFunctions();

        // adding the current imported module to the map
        moduleFunctionMap.put(nameOfModule, functionsOfModule);

        // getting the names of the modules we want to import
        Set<String> namesOfModuleToImport = moduleBoNode.getUsedModules();

        if (boProject.isExternalModule(nameOfModule) && CollectionUtils.isNotEmpty(namesOfModuleToImport)) {
            throw new BoProjectException("External dependencies are not allowed to have imports! Please properly export the module you want to use!");
        }

        if (CollectionUtils.isNotEmpty(namesOfModuleToImport)) {
            // we have imports to do!
            // circular dependencies are not a problem since as soon as its once seen its get added before
            // see few lines above

            for (String nameOfModuleIteration : namesOfModuleToImport) {
                moduleFunctionMap = performImportForModule(boProject, moduleFunctionMap, nameOfModuleIteration);
            }
        }

        return moduleFunctionMap;
    }

}
