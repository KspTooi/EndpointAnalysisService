package com.ksptooi.utils;

import com.ksptooi.mybatis.model.po.TableField;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseToolsH2 {

    private final Connection conn;


    // 类型映射表
    private final HashMap<String, String> typeMapper = new HashMap<>();

    private final HashMap<String, String> reverseTypeMapper = new HashMap<>();

    public DatabaseToolsH2(Connection conn) {
        this.conn = conn;
        initTypeMapper();
    }

    private void initTypeMapper() {

        // H2 / ANSI Type Mapping
        typeMapper.put("VARCHAR", "String");
        typeMapper.put("CHARACTER VARYING", "String");
        typeMapper.put("VARCHAR_IGNORECASE", "String");

        typeMapper.put("CHAR", "String");
        typeMapper.put("CHARACTER", "String");

        typeMapper.put("TEXT", "String");
        typeMapper.put("CLOB", "String");

        typeMapper.put("JSON", "String");
        typeMapper.put("UUID", "String");
        typeMapper.put("ARRAY", "String");
        typeMapper.put("OTHER", "String");

        typeMapper.put("INT", "Integer");
        typeMapper.put("INTEGER", "Integer");
        typeMapper.put("TINYINT", "Integer");
        typeMapper.put("SMALLINT", "Short");
        typeMapper.put("BIGINT", "Long");

        typeMapper.put("BOOLEAN", "Boolean");
        typeMapper.put("BIT", "Boolean");

        typeMapper.put("REAL", "Float");
        typeMapper.put("DOUBLE", "Double");
        typeMapper.put("DOUBLE PRECISION", "Double");
        typeMapper.put("FLOAT", "Double");

        typeMapper.put("DECIMAL", "BigDecimal");
        typeMapper.put("NUMERIC", "BigDecimal");

        typeMapper.put("BINARY", "Byte[]");
        typeMapper.put("VARBINARY", "Byte[]");
        typeMapper.put("BINARY VARYING", "Byte[]");
        typeMapper.put("BLOB", "Byte[]");

        typeMapper.put("DATE", "LocalDateTime");
        typeMapper.put("TIME", "LocalDateTime");
        typeMapper.put("TIME WITH TIME ZONE", "LocalDateTime");
        typeMapper.put("TIMESTAMP", "LocalDateTime");
        typeMapper.put("TIMESTAMP WITH TIME ZONE", "LocalDateTime");

        for (Map.Entry<String, String> entry : typeMapper.entrySet()) {
            reverseTypeMapper.put(entry.getValue(), entry.getKey());
        }

    }

    public String typeToDatabase(String s) {
        return reverseTypeMapper.get(s);
    }

    public List<TableField> getFieldsByTable(String dbName, String tableName) {

        List<TableField> tableFields = new ArrayList<>();
        try {
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            String schemaName = "PUBLIC";
            String tableNameUpper = StringUtils.isNotBlank(tableName) ? tableName.toUpperCase() : tableName;
            ResultSet resultSet = databaseMetaData.getColumns(null, schemaName, tableNameUpper, null);
            while (resultSet.next()) {

                String name = resultSet.getString("COLUMN_NAME");
                String type = resultSet.getString("TYPE_NAME");
                String comment = resultSet.getString("REMARKS");

                TableField tableField = new TableField();
                tableField.setName(name);
                tableField.setJavaFieldName(toJavaName(name));
                tableField.setJavaGetterName(toJavaGetterName(toJavaName(name)));
                tableField.setType(type);

                String normalizedType = StringUtils.isNotBlank(type) ? type.toUpperCase() : "";
                String javaType = typeMapper.get(normalizedType);
                if (StringUtils.isBlank(javaType)) {
                    javaType = "String";
                }
                tableField.setJavaType(javaType);

                if (StringUtils.isNotBlank(comment)) {
                    tableField.setComment(comment);
                }
                if (StringUtils.isBlank(comment)) {
                    tableField.setComment("");
                }
                tableFields.add(tableField);
            }
            resultSet.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return tableFields;
    }

    private String resolveSchema(String dbName) {
        if (StringUtils.isNotBlank(dbName)) {
            return dbName.toUpperCase();
        }
        try {
            String schema = conn.getSchema();
            if (StringUtils.isNotBlank(schema)) {
                return schema.toUpperCase();
            }
        } catch (SQLException ignored) {
        }
        return "PUBLIC";
    }


    private String toJavaGetterName(String fieldName) {
        char[] chars = fieldName.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    private String toJavaName(String fieldName) {
        String[] words = fieldName.split("_");
        StringBuilder javaName = new StringBuilder(words[0].toLowerCase());
        for (int i = 1; i < words.length; i++) {
            javaName.append(Character.toUpperCase(words[i].charAt(0)))
                    .append(words[i].substring(1).toLowerCase());
        }
        return javaName.toString();
    }

    /**
     * 判断表是否存在
     */
    public boolean tableExist(String tableName) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet resultSet = metaData.getTables(null, null, tableName.toUpperCase(), new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    /**
     * 删除一张表
     */
    public void dropTable(String tableName) throws SQLException {
        String dropTableSQL = "DROP TABLE IF EXISTS " + tableName;
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(dropTableSQL);
        }
    }

    /**
     * 判断表中是否有数据
     */
    public boolean isTableEmpty(String tableName) throws SQLException {
        String query = "SELECT 1 FROM " + tableName + " LIMIT 1";
        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            return !resultSet.next();
        }
    }

}
