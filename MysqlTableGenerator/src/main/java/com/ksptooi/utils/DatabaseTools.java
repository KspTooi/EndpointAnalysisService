package com.ksptooi.utils;

import com.ksptooi.mybatis.model.po.TableField;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseTools {

    private final Connection conn;


    //类型映射表
    private final HashMap<String,String> typeMapper = new HashMap<>();

    private final HashMap<String,String> reverseTypeMapper = new HashMap<>();

    public DatabaseTools(Connection conn){
        this.conn = conn;
        initTypeMapper();
    }

    private void initTypeMapper(){

        //MySQL to Java Type Mapping
        typeMapper.put("VARCHAR", "String");
        typeMapper.put("INT", "Integer");
        typeMapper.put("BIT", "Boolean");
        typeMapper.put("SMALLINT", "Short");
        typeMapper.put("REAL", "Float");
        typeMapper.put("DOUBLE", "Double");
        typeMapper.put("BIGINT", "Long");
        typeMapper.put("DECIMAL", "BigDecimal");
        typeMapper.put("BINARY", "Byte[]");
        typeMapper.put("DATETIME", "Date");
        typeMapper.put("TINYINT", "Integer");
        typeMapper.put("TEXT", "String");
        typeMapper.put("JSON", "String");

        for(Map.Entry<String,String> entry : typeMapper.entrySet()){
            reverseTypeMapper.put(entry.getValue(),entry.getKey());
        }

    }

    public String typeToDatabase(String s){
        return reverseTypeMapper.get(s);
    }

    public List<TableField> getFieldsByTable(String dbName,String tableName){

        List<TableField> tableFields = new ArrayList<>();
        try {
            DatabaseMetaData databaseMetaData = conn.getMetaData();

            ResultSet resultSet = databaseMetaData.getColumns(dbName, dbName, tableName, null);
            while(resultSet.next()){

                String name = resultSet.getString("COLUMN_NAME");
                String type = resultSet.getString("TYPE_NAME");
                String comment = resultSet.getString("REMARKS");

                TableField tableField = new TableField();
                tableField.setName(name);
                tableField.setJavaFieldName(toJavaName(name));
                tableField.setJavaGetterName(toJavaGetterName(toJavaName(name)));
                tableField.setType(type);
                tableField.setJavaType(typeMapper.get(type));
                tableField.setComment(comment);
                if(comment == null || comment.isBlank()){
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


    private String toJavaGetterName(String fieldName){
        char[] chars = fieldName.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    private String toJavaName(String fieldName){
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
        try (ResultSet resultSet = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
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
