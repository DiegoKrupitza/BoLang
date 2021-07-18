package com.diegokrupitza.bolang.syntaxtree.nodes.stat;

import com.diegokrupitza.bolang.syntaxtree.nodes.ExpressionNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 08.07.21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IfNode extends ExpressionNode {

    private ExpressionNode condition;

    private List<ExpressionNode> ifSection;

}
