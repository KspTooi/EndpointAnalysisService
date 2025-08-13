package com.ksptooi.commons.parser;

import java.util.ArrayList;
import java.util.List;

public class ClassField {

    private String name;
    private String value;
    private String type;
    private String comment;

    private String kind;        //字段上使用$标记的首个Kind
    private List<String> kinds = new ArrayList<>(); //字段上全部使用$标记的Kind

    private FieldAnnotation annotation;
    private List<FieldAnnotation> annotations = new ArrayList<>();

    public boolean hasAnno(String name){
        for(var a : annotations){
            if(a.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    public FieldAnnotation getAnno(String name){
        for(var a : annotations){
            if(a.getName().equalsIgnoreCase(name)){
                return a;
            }
        }
        return null;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<String> getKinds() {
        return kinds;
    }

    public void setKinds(List<String> kinds) {
        this.kinds = kinds;
    }

    public FieldAnnotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(FieldAnnotation annotation) {
        this.annotation = annotation;
    }

    public List<FieldAnnotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<FieldAnnotation> annotations) {
        this.annotations = annotations;
    }
}
