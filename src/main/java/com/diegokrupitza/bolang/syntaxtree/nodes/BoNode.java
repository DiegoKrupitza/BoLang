package com.diegokrupitza.bolang.syntaxtree.nodes;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 08.07.21
 */
@Data
public class BoNode extends ExpressionNode {

    private List<ExpressionNode> stats;
    private Set<String> usedModules;

    public BoNode() {
    }

    public BoNode(List<ExpressionNode> stats) {
        this.stats = stats;
    }

    public BoNode(List<ExpressionNode> stats, Set<String> usedModules) {
        this.stats = stats;
        this.usedModules = usedModules;
    }
}
