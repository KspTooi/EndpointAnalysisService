package com.ksptooi.nuclear.vue.models;

public class TsField {
    private String name;
    private String options;
    private String type;
    private String comment;
    private String originType;

    public TsField(){
    }

    public TsField(String name, String options, String type, String comment) {
        this.name = name;
        this.options = options;
        this.type = type;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
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

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }
}
