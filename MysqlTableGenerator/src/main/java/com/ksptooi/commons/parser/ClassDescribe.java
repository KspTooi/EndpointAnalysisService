package com.ksptooi.commons.parser;

import java.util.List;

public class ClassDescribe {
    
    private String className;

    private List<ClassField> fields;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<ClassField> getFields() {
        return fields;
    }

    public void setFields(List<ClassField> fields) {
        this.fields = fields;
    }
}
