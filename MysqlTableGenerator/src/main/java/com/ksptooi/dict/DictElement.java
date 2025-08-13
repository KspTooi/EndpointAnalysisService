package com.ksptooi.dict;

public class DictElement {

    private String pk;
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
