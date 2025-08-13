package com.ksptooi.commons;

import java.util.HashMap;

public class JavaToTsTypeMapper implements TypeMapper{

    private HashMap<String,String> t = new HashMap<>();

    private HashMap<String,TypeFieldLength> l = new HashMap<>();

    public JavaToTsTypeMapper(){
        initMapper();
        initLengthMapper();
    }

    @Override
    public String getTypeMapperName() {
        return "JavaToMysqlTypeMapper";
    }

    @Override
    public void initMapper() {
        t.put("byte","String");
        t.put("short","String");
        t.put("int","INT");
        t.put("long","BIGINT");
        t.put("float","DOUBLE");
        t.put("double","DOUBLE");
        t.put("boolean","INT");
        t.put("char","VARCHAR");
        t.put("String","VARCHAR");
        t.put("Byte","TINYINT");
        t.put("Short","TINYINT");
        t.put("Integer","INT");
        t.put("Long","BIGINT");
        t.put("Float","DOUBLE");
        t.put("Double","DOUBLE");
        t.put("Boolean","INT");
        t.put("Char","VARCHAR");
        t.put("Date","DATETIME");
        t.put("Integer[]","INT");
    }

    private void initLengthMapper(){
        l.put("VARCHAR",new TypeFieldLength(255,-1));
        l.put("INT",new TypeFieldLength(-1,-1));
        l.put("DATETIME",new TypeFieldLength(-1,-1));
        l.put("TINYINT",new TypeFieldLength(-1,-1));
        l.put("DECIMAL",new TypeFieldLength(65,2));
        l.put("BIGINT",new TypeFieldLength(-1,-1));
        l.put("DOUBLE",new TypeFieldLength(-1,-1));
        l.put("DATE",new TypeFieldLength(-1,-1));
    }

    @Override
    public String convert(String str) {
        String s = t.get(str);
        if(s == null){
            throw new RuntimeException("无法找到类型映射 输入类型:" + str);
        }
        return s;
    }

    @Override
    public TypeFieldLength getFieldLength(String type) {

        TypeFieldLength ret = l.get(type);

        if(ret == null){
            throw new RuntimeException("无法找到类型长度映射 输入类型:" + type);
        }

        return ret;
    }
}
