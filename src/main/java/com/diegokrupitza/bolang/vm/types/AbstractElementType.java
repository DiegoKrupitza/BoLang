package com.diegokrupitza.bolang.vm.types;

import com.diegokrupitza.bolang.vm.utils.Arrays;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.Objects;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 09.07.21
 */
@Data
@AllArgsConstructor
public abstract class AbstractElementType<T> {

    @NonNull
    private T value;

    @NonNull
    private Type type;

    @Override
    public String toString() {
        switch (type) {
            case DOUBLE:
                return ((Double) value) + "";
            case INTEGER_NUMBER:
                return ((Integer) value) + "";
            case ARRAY:
                return Arrays.toString((ArrayElement) this);
            case STRING:
                return "\"" + ((String) value) + "\"";
            case BOOLEAN:
                return ((Boolean) value).toString();
        }
        return value + "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractElementType)) return false;

        AbstractElementType<?> that = (AbstractElementType<?>) o;

        // check to make for example 10.0 equals to 10
        // this has to be done extra since one will be NumberType and the other will be Integertype
        if ((that.getType() == Type.DOUBLE && getType() == Type.INTEGER_NUMBER)) {
            DoubleElement thathCasted = (DoubleElement) that;
            IntegerElement thisCasted = (IntegerElement) this;

            return thathCasted.getValue().doubleValue() == thisCasted.getValue().intValue();
        } else if ((that.getType() == Type.INTEGER_NUMBER && getType() == Type.DOUBLE)) {
            IntegerElement thathCasted = (IntegerElement) that;
            DoubleElement thisCasted = (DoubleElement) this;

            return thathCasted.getValue().intValue() == thisCasted.getValue().doubleValue();
        }

        return getValue().equals(that.getValue()) &&
                getType() == that.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue(), getType());
    }
}
