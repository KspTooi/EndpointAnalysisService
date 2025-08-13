package com.ksptooi.nuclear.vue.models;

public class OpsFormRules {
    
    public String fieldName; //字段名
    public String type; //类型
    public String required; //是否必填 true false
    public String hint; //校验失败提示

    
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}