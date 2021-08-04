package com.diegokrupitza.bolang.syntaxtree.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 28.07.21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionNode extends ExpressionNode {

    private String name;

    private List<String> paramNames;

    private List<ExpressionNode> body;

    @Override
    public boolean equals(Object o) {
        // functions are equals when they have the same name and params
        if (this == o) return true;
        if (!(o instanceof FunctionNode)) return false;
        FunctionNode that = (FunctionNode) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getParamNames(), that.getParamNames());
    }

    @Override
    public int hashCode() {
        // functions hash is only name and params
        return Objects.hash(getName(), getParamNames());
    }
}
