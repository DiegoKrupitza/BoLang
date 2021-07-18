package com.diegokrupitza.bolang.vm.functions.impl;

import com.diegokrupitza.bolang.vm.functions.BoFunction;
import com.diegokrupitza.bolang.vm.functions.Function;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionException;
import com.diegokrupitza.bolang.vm.functions.exceptions.BoFunctionParameterException;
import com.diegokrupitza.bolang.vm.types.AbstractElementType;
import com.diegokrupitza.bolang.vm.types.StringElement;
import com.diegokrupitza.bolang.vm.types.Type;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 10.07.21
 */
@BoFunction(name = "date")
@Getter
public class DateFunction implements Function {

    // default format 2007-12-03T10:15:30
    private String dateFormat = null;

    @Override
    public void paramCheck(List<AbstractElementType<?>> params) throws BoFunctionException {
        // date function takes one param or none
        // if one param is given it has to be a string
        if (CollectionUtils.isEmpty(params)) {
            return;
        }

        // when there is a param its only allowed to be one and it has to be a string
        if (params.size() == 1 && params.get(0).getType() == Type.STRING) {
            this.dateFormat = ((StringElement) params.get(0)).getValue();
        } else {
            throw new BoFunctionParameterException("The function `date` is only allowed to be called with a string parameter that describes the format or no parameter");
        }
    }

    @Override
    public AbstractElementType<?> execute(List<AbstractElementType<?>> params) throws BoFunctionException {
        LocalDateTime now = LocalDateTime.now();
        String strDate = now.toString();

        // getting the date in a format
        if (this.dateFormat != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(this.dateFormat);
            strDate = now.format(formatter);
        }

        return new StringElement(strDate);
    }
}
