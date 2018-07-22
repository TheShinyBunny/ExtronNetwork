package com.extron.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Completions {

    String[] values() default "";

    Class<? extends SenderCompletion> getter() default SenderCompletion.class;

}
