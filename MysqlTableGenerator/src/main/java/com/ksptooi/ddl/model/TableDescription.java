package com.ksptooi.ddl.model;

import java.util.ArrayList;
import java.util.List;

public class TableDescription {

    private String tableName;

    private List<TableDescriptionField> fields;

    private List<String> primary = new ArrayList<>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TableDescriptionField> getFields() {
        return fields;
    }

    public void setFields(List<TableDescriptionField> fields) {
        this.fields = fields;
    }

    public List<String> getPrimary() {
        return primary;
    }

    public void setPrimary(List<String> primary) {
        this.primary = primary;
    }

}
