package com.diegokrupitza.bolang.syntaxtree.nodes.data;

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
public class IdNode extends DataNode {

    private String name;

}
