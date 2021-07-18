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
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReturnNode extends ExpressionNode {

    private ExpressionNode ret;

}
