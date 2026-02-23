package com.ksptool.bio.commons.dataprocess.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义校验注解，用于校验字符串是否在给定的数组中
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InValidator.class) // 关键：指定此注解的校验逻辑由 InValidator.class 实现
public @interface In {

    String[] value() default {};

    String message() default "参数值错误,可选的值:{values}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
