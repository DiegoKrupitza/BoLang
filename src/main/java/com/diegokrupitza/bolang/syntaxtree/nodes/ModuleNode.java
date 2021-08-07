package com.diegokrupitza.bolang.syntaxtree.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 04.08.21
 */
@Data
@AllArgsConstructor
public class ModuleNode extends ExpressionNode {

    private String name;

    private List<ImportNode> imports;

    private List<FunctionNode> functions;
}
