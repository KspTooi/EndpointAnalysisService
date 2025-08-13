package com.ksptooi.commons;

public interface TypeMapper {

    public String getTypeMapperName();

    public void initMapper();

    public String convert(String type);

    public TypeFieldLength getFieldLength(String type);
}
