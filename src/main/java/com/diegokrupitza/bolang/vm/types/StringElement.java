package com.diegokrupitza.bolang.vm.types;

import com.diegokrupitza.bolang.vm.VirtualMachineException;
import lombok.NonNull;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 11.07.21
 */
public class StringElement extends AbstractElementType<String> {

    public StringElement() {
        super("", Type.STRING);
    }

    public StringElement(@NonNull String value) {
        super(value, Type.STRING);
    }

    public StringElement get(int index) throws VirtualMachineException {
        if (index < 0 || index >= this.getValue().length()) {
            throw new VirtualMachineException(String.format("Index has to be in range 0 to %d ", this.getValue().length() - 1));
        }

        return new StringElement(this.getValue().charAt(index) + "");
    }
}
