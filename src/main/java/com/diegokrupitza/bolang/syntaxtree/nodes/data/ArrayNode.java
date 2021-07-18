package com.diegokrupitza.bolang.syntaxtree.nodes.data;

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
public class ArrayNode extends DataNode {

    private List<ExpressionNode> content;

}
