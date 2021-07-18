package com.diegokrupitza.bolang.syntaxtree.nodes.stat;

import com.diegokrupitza.bolang.syntaxtree.nodes.ExpressionNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 08.07.21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignVariableNode extends ExpressionNode {

    private String identifierName;

    private ExpressionNode expr;

}
