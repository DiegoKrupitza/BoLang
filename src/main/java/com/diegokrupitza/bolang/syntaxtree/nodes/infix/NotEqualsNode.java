package com.diegokrupitza.bolang.syntaxtree.nodes.infix;

import com.diegokrupitza.bolang.syntaxtree.nodes.ExpressionNode;
import lombok.Data;
import lombok.ToString;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 08.07.21
 */
@Data
@ToString(callSuper = true)
public class NotEqualsNode extends InfixNode {
    public NotEqualsNode(ExpressionNode left, ExpressionNode right) {
        super(left, right);
    }

    public NotEqualsNode() {
    }
}
