package com.diegokrupitza.bolang.syntaxtree.nodes;

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
public class CallFunctionNode extends ExpressionNode {

    private String name;

    private String module;

    private List<ExpressionNode> params;

}
