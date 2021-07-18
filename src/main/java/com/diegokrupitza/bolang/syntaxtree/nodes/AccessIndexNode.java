package com.diegokrupitza.bolang.syntaxtree.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 08.07.21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessIndexNode extends ExpressionNode {

    private String identifierName;

    private ExpressionNode index;

}
