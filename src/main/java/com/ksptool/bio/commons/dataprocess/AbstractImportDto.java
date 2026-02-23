package com.ksptool.bio.commons.dataprocess;

import com.ksptool.bio.commons.dataprocess.converter.IntegerRow;
import com.ksptool.bio.commons.dataprocess.converter.StringRow;

/**
 * 导入数据验证基类
 * 所有导入数据验证类都继承自此基类
 * 所有导入数据验证类都实现validate方法
 */
public abstract class AbstractImportDto {

    /**
     * 验证导入数据
     *
     * @return 验证结果 如果验证通过则返回null 否则返回错误信息
     */
    public abstract String validate();

    // 您可以把这个方法放在 AbstractImportDto 里，或者一个单独的 ValidatorUtils 类里
    protected String validateAnnotations() {
        Class<?> clazz = this.getClass();
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();

        for (java.lang.reflect.Field field : fields) {
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(this);
            } catch (IllegalAccessException e) {
                continue;
            }

            // 1. 检查 StringRow 注解
            StringRow stringRow = field.getAnnotation(StringRow.class);
            if (stringRow != null) {
                // 校验必填
                if (stringRow.required() && (value == null || value.toString().trim().isEmpty())) {
                    return formatError(field, stringRow.message(), "不能为空");
                }
                // 校验长度 (仅当值存在时)
                if (value != null) {
                    String strVal = value.toString();
                    if (stringRow.max() > 0 && strVal.length() > stringRow.max()) {
                        return formatError(field, stringRow.message(), "长度不能超过" + stringRow.max());
                    }
                }
            }

            // 2. 检查 IntegerRow 注解
            IntegerRow integerRow = field.getAnnotation(IntegerRow.class);
            if (integerRow != null) {
                // 校验必填
                if (integerRow.required() && value == null) {
                    return formatError(field, integerRow.message(), "不能为空");
                }
                // 这里的格式校验(format)其实Converter已经做过了，如果Converter没报错且值不为空，说明格式是对的
                // 所以这里只需要校验 null
            }
        }
        return null;
    }

    private String formatError(java.lang.reflect.Field field, String customMsg, String defaultMsg) {
        if (customMsg != null && !customMsg.isEmpty()) {
            return customMsg;
        }
        // 获取 ExcelProperty 里的名字作为提示
        com.alibaba.excel.annotation.ExcelProperty property = field.getAnnotation(com.alibaba.excel.annotation.ExcelProperty.class);
        String name = (property != null && property.value().length > 0) ? property.value()[0] : field.getName();
        return name + defaultMsg;
    }
}
