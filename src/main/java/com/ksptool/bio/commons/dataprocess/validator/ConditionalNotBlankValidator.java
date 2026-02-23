package com.ksptool.bio.commons.dataprocess.validator;


import com.ksptool.bio.commons.dataprocess.Str;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConditionalNotBlankValidator implements ConstraintValidator<ConditionalNotBlank, Object> {

    //触发字段
    private String trigger;

    //必填字段
    private String field;

    @Override
    public void initialize(ConditionalNotBlank constraintAnnotation) {
        this.trigger = constraintAnnotation.trigger();
        this.field = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        if (Str.isBlank(trigger) || Str.isBlank(field)) {
            return true;
        }

        try {
            var clazz = value.getClass();
            var fieldObj = clazz.getDeclaredField(field);

            //获取触发字段值
            var triggerField = clazz.getDeclaredField(trigger);
            triggerField.setAccessible(true);

            var triggerValue = triggerField.get(value);
            var fieldValue = fieldObj.get(value);

            //触发值与必填值类型必须为字符串
            if (!(triggerValue instanceof String) || !(fieldValue instanceof String)) {
                throw new IllegalArgumentException("trigger字段值与field字段值类型必须为String");
            }

            var triggerValueStr = triggerValue.toString();
            var fieldValueStr = fieldValue.toString();

            //如果触发值为空，则必填值必须为空
            if (Str.isBlank(triggerValueStr)) {
                if (Str.isBlank(fieldValueStr)) {
                    return true;
                }
                //判断是否有{trigger}占位符
                var triggerTemplate = context.getDefaultConstraintMessageTemplate();
                if (triggerTemplate != null && triggerTemplate.contains("{trigger}")) {
                    var message = triggerTemplate.replace("{trigger}", trigger);
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(message)
                            .addConstraintViolation();
                }
                //判断是否有{field}占位符
                var fieldTemplate = context.getDefaultConstraintMessageTemplate();
                if (fieldTemplate != null && fieldTemplate.contains("{field}")) {
                    var message = fieldTemplate.replace("{field}", field);
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(message)
                            .addConstraintViolation();
                }
                return false;
            }

            //如果触发值不为空，则必填值必须不为空
            if (!Str.isBlank(triggerValueStr)) {
                if (Str.isBlank(fieldValueStr)) {
                    //判断是否有{trigger}占位符
                    var triggerTemplate = context.getDefaultConstraintMessageTemplate();
                    if (triggerTemplate != null && triggerTemplate.contains("{trigger}")) {
                        var message = triggerTemplate.replace("{trigger}", trigger);
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(message)
                                .addConstraintViolation();
                    }
                    //判断是否有{field}占位符
                    var fieldTemplate = context.getDefaultConstraintMessageTemplate();
                    if (fieldTemplate != null && fieldTemplate.contains("{field}")) {
                        var message = fieldTemplate.replace("{field}", field);
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(message)
                                .addConstraintViolation();
                    }
                    return false;
                }
                return true;
            }

            return true;
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addConstraintViolation();
            return false;
        }
    }


}
