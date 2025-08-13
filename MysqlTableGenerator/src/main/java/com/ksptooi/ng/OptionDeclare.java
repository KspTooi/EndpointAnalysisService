package com.ksptooi.ng;

import org.apache.commons.lang3.StringUtils;

public class OptionDeclare {

    private String key;
    private String example;
    private String title;
    private boolean required = false;

    public String getKey() {
        if(StringUtils.isBlank(key)){
            return "?";
        }
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        if(StringUtils.isBlank(title)){
            return "?";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExample() {
        if(StringUtils.isBlank(example)){
            return "?";
        }
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public boolean isRequired() {
        return required;
    }
    public String getRequired() {
        if(required){
            return "必填";
        }
        return "";
    }


    public void setRequired(boolean required) {
        this.required = required;
    }
}
