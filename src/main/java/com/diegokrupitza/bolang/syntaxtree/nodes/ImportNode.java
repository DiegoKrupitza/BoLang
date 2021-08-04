package com.diegokrupitza.bolang.syntaxtree.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 04.08.21
 */
@Data
@AllArgsConstructor
public class ImportNode extends ExpressionNode {

    private String moduleName;
}
