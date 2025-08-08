package com.ksptooi.commons.dataprocess.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InValidator implements ConstraintValidator<In, String> {

    private final Set<String> allowedValues = new HashSet<>();

    @Override
    public void initialize(In constraintAnnotation) {
        var val = constraintAnnotation.value();
        if(val == null){
            return;
        }
        allowedValues.addAll(Arrays.asList(val));
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        //不处理空值
        if(value==null){
            return true;
        }

        if(allowedValues.isEmpty()){
            return true;
        }

        if(allowedValues.contains(value)){
            return true;
        }

        //设置错误信息，替换{values}占位符
        
        //判断是否有{values}占位符
        var template = context.getDefaultConstraintMessageTemplate();

        if(template!=null && template.contains("{values}")){
            var valuesStr = String.join(",", allowedValues);
            var message = template.replace("{values}", valuesStr);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        }

        return false;
    }

}
