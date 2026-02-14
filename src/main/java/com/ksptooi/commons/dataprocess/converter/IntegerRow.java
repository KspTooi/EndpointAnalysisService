package com.ksptooi.commons.dataprocess.converter;

import java.lang.annotation.*;

/**
 * 自定义注解，用于标识Excel行
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface IntegerRow {

    //是否必填
    boolean required() default false;

    /* 映射规则
     * 规则: 0=男;1=女;2=其他 分号分割
     */
    String format() default "";

    //错误信息
    String message() default "";

}
