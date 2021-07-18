package com.diegokrupitza.bolang.symboltable;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 08.07.21
 */
@Data
@NoArgsConstructor
public class BoSymbolTable {

    private List<String> sym = new LinkedList<>();
    private BoSymbolTable parent = null;

    public void add(String idName) {
        this.sym.add(idName);
    }

    /**
     * Generate a new subscope based on the current object
     *
     * @return the newly created SymbolTable scope with the current object set as parent
     */
    public BoSymbolTable createScope() {
        BoSymbolTable scope = new BoSymbolTable();
        scope.setParent(this);
        return scope;
    }

    public boolean inScope(String name) {
        if (this.sym.contains(name)) {
            return true;
        }

        if (this.parent != null) {
            // we have a parent this means we need to check there too
            return this.parent.inScope(name);
        }

        return false;
    }
}
