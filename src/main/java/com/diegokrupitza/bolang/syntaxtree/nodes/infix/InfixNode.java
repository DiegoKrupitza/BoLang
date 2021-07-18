package com.diegokrupitza.bolang.syntaxtree.nodes.infix;

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
public abstract class InfixNode extends ExpressionNode {

    private ExpressionNode left;

    private ExpressionNode right;

}
