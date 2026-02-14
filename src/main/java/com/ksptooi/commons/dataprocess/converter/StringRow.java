package com.ksptooi.commons.dataprocess.converter;

import java.lang.annotation.*;

/**
 * 自定义注解，用于标识Excel行
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface StringRow {

    //是否必填
    boolean required() default false;

    //最大长度
    int max() default 0;

    //最小长度
    int min() default 0;

    //错误信息
    String message() default "";

}
