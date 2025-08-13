package com.ksptooi.commons.parser;

import java.util.List;

public class FieldAnnotation {

    private String name;
    private String defaultVal;
    private List<String> vals;

    public FieldAnnotation(String name){
        this.name = name;
    }

    public FieldAnnotation(String name,String defaultVal){
        this.name = name;
    }

    public FieldAnnotation(){

    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDefaultVal() {
        return defaultVal;
    }
    public void setDefaultVal(String defaultVal) {
        this.defaultVal = defaultVal;
    }
    public List<String> getVals() {
        return vals;
    }
    public void setVals(List<String> vals) {
        this.vals = vals;
    }

}
