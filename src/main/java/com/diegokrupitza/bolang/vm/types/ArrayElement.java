package com.diegokrupitza.bolang.vm.types;

import com.diegokrupitza.bolang.vm.VirtualMachineException;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
public class ArrayElement extends AbstractElementType<List<AbstractElementType<?>>> {

    public ArrayElement() {
        super(new ArrayList<>(), Type.ARRAY);
    }

    public ArrayElement(@NonNull List<AbstractElementType<?>> value) {
        super(value, Type.ARRAY);
    }

    public void add(AbstractElementType<?> value) {
        this.getValue().add(value);
    }

    public void addAll(List<AbstractElementType<?>> collection) {
        this.getValue().addAll(collection);
    }

    public long size() {
        return this.getValue().size();
    }

    public AbstractElementType<?> get(int index) throws VirtualMachineException {
        if (index < 0 || index >= this.getValue().size()) {
            throw new VirtualMachineException(String.format("Index has to be in range 0 to %d ", this.getValue().size() - 1));
        }
        return this.getValue().get(index);
    }

    public List<AbstractElementType<?>> getFromIndex(int i) {
        return this.getValue().subList(i, this.getValue().size());
    }
}
