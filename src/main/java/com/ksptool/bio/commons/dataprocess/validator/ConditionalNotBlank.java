package com.ksptool.bio.commons.dataprocess.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConditionalNotBlankValidator.class)
@Repeatable(ConditionalNotBlanks.class)
public @interface ConditionalNotBlank {

    String message() default "当填写{trigger}时，{field}必填";

    //触发条件
    String trigger();

    //必填字段
    String field();

    //分组
    Class<?>[] groups() default {};

    //负载
    Class<? extends Payload>[] payload() default {};

}

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConditionalNotBlankValidator.class)
@interface ConditionalNotBlanks {
    ConditionalNotBlank[] value();
}
