package com.ksptool.bio.commons.dataprocess.validator;

import jakarta.validation.*;

import java.util.Set;

public class Valid {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator jsr303 = factory.getValidator();

    /**
     * 校验对象，如果校验不通过，抛出异常
     *
     * @param object 待校验对象
     */
    public static void withException(Object object) {

        // 校验对象，获取校验结果
        Set<ConstraintViolation<Object>> violations = jsr303.validate(object);

        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder();


            var count = violations.size();
            var current = 0;

            for (ConstraintViolation<Object> violation : violations) {
                current++;

                //第一条数据换行
                if (current == 1) {
                    message.append("\r\n");
                }

                message.append("字段 ").append(violation.getPropertyPath()).append(" 校验失败:").append(violation.getMessage());

                //最后一条数据不换行
                if (current != count) {
                    message.append("\r\n");
                }

            }

            throw new ConstraintViolationException(message.toString(), violations);
        }
    }

    /**
     * 校验对象，如果校验不通过，返回错误信息
     *
     * @param object 待校验对象
     * @return 错误信息，如果校验通过，返回null
     */
    public static String withMessage(Object object) {
        Set<ConstraintViolation<Object>> violations = jsr303.validate(object);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder();

            var count = violations.size();
            var current = 0;

            for (ConstraintViolation<Object> violation : violations) {
                current++;
                if (current == 1) {
                    message.append("\r\n");
                }
                message.append("字段 ").append(violation.getPropertyPath()).append(" 校验失败:").append(violation.getMessage());
                if (current != count) {
                    message.append("\r\n");
                }
            }
            return message.toString();
        }
        return null;
    }

    /**
     * 校验对象，如果校验不通过，返回第一个错误信息
     *
     * @param object    待校验对象
     * @param separator 错误信息分隔符
     * @return 错误信息，如果校验通过，返回null
     */
    public static String withFirstMessage(Object object, String separator) {
        Set<ConstraintViolation<Object>> violations = jsr303.validate(object);
        if (!violations.isEmpty()) {
            return "字段 " + violations.iterator().next().getPropertyPath() + " 校验失败:" + violations.iterator().next().getMessage() + separator;
        }
        return null;
    }

    /**
     * 校验对象，如果校验不通过，返回false
     *
     * @param object 待校验对象
     * @return 校验是否通过
     */
    public static boolean withBoolean(Object object) {
        return jsr303.validate(object).isEmpty();
    }


}
