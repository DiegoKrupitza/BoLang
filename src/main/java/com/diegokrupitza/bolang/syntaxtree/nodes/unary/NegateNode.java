package com.diegokrupitza.bolang.syntaxtree.nodes.unary;

import com.diegokrupitza.bolang.syntaxtree.nodes.ExpressionNode;
import lombok.Data;
import lombok.ToString;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 09.07.21
 */
@Data
@ToString(callSuper = true)
public class NegateNode extends ExpressionNode {

    private ExpressionNode inner;

    public NegateNode(ExpressionNode inner) {
        this.inner = inner;
    }

}
