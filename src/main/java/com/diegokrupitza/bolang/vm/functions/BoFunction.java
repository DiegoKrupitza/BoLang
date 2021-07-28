package com.diegokrupitza.bolang.vm.functions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 10.07.21
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BoFunction {
    String name();

    String module();

    String description() default "";
}
