package com.diegokrupitza.bolang.syntaxtree.nodes.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 13.07.21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegerNode extends DataNode {

    private Integer value;

}
