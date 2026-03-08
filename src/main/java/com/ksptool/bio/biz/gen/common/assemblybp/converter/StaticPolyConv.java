package com.ksptool.bio.biz.gen.common.assemblybp.converter;


import com.ksptool.bio.biz.gen.common.assemblybp.entity.blueprint.PolyBlueprint;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.blueprint.RawBlueprint;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.field.PolyField;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.field.PolyTable;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.field.RawField;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.field.RawTable;
import com.ksptool.bio.biz.gen.common.assemblybp.utils.NamesTool;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 静态字段的聚合转换器
 */
public class StaticPolyConv implements PolyConverter {

    private static final Logger logger = LoggerFactory.getLogger(StaticPolyConv.class);

    private List<String> tablePrefixes = new ArrayList<>();
    private List<String> fieldPrefixes = new ArrayList<>();

    //静态字段
    private List<PolyField> staticFields = new ArrayList<>();

    public StaticPolyConv() {
    }


    /**
     * 添加静态字段
     * @param polyField 静态字段
     */
    public void addStaticField(PolyField polyField) {
        if (polyField == null){
            return;
        }
        staticFields.add(polyField);
    }


    @Override
    public PolyTable toPolyTable(RawTable rawTable) {
        if (rawTable == null) return null;

        PolyTable polyTable = new PolyTable();
        polyTable.setRawTable(rawTable);

        // 设置标准名称 (PascalCase)
        String className = toPascalCase(removeTablePrefixes(rawTable.getName()));
        polyTable.setStdName(className);
        polyTable.setComment(rawTable.getComment());

        // 处理字段
        polyTable.setFields(staticFields);

        //匹配主键 如果数据库里面的主键与静态字段的主键一致,则设置为主键
        for(var rawField : rawTable.getFields()){

            if(rawField.isPrimaryKey()){
                //遍历staticFields,如果名称一致,则设置为主键
                for(var staticField : staticFields){
                    if(staticField.getStdName().equalsIgnoreCase(NamesTool.toPascalCase(rawField.getName()))){
                        staticField.setPrimaryKey(true);
                        break;
                    }
                }
            }

        }

        // 前端通常不需要像Java那样复杂的Import管理，这里留空或按需添加
        polyTable.setImports(new ArrayList<>());
        return polyTable;
    }

    @Override
    public PolyField toPolyField(RawField rawField) {
        if (rawField == null) return null;

        PolyField polyField = new PolyField();
        polyField.setRawField(rawField);

        // 字段名转小驼峰
        String fieldName = toCamelCase(removeFieldPrefixes(rawField.getName()));
        polyField.setStdName(capitalize(fieldName)); // StdName通常是大写开头，用于拼接get/set，前端可能用不到
        //polyField.setPfscn(fieldName); // 设置小驼峰名称供模板使用

        // 类型转换
        //polyField.setType(toDynamicType(rawField.getType()));
        polyField.setComment(rawField.getComment());
        polyField.setRequired(rawField.isRequired());
        polyField.setPrimaryKey(rawField.isPrimaryKey());
        return polyField;
    }

    @Override
    public PolyBlueprint toPolyBlueprint(RawBlueprint rawBlueprint) {
        // 复用通用的蓝图转换逻辑 (读取文件内容)
        if (rawBlueprint == null) return null;
        try {
            Path filePath = rawBlueprint.getFilePath();
            if (!Files.exists(filePath)) return null;

            PolyBlueprint polyBlueprint = new PolyBlueprint();
            polyBlueprint.setRawBlueprint(rawBlueprint);
            polyBlueprint.setTemplateContent(Files.readString(filePath));
            polyBlueprint.setRelativePathWithPlaceholder(rawBlueprint.getRelativeFilePath());
            polyBlueprint.setFileNameWithPlaceholder(rawBlueprint.getFileName());
            return polyBlueprint;
        } catch (IOException e) {
            logger.error("读取蓝图失败", e);
            return null;
        }
    }

    @Override
    public void removeTablePrefixes(String... prefixes) {
        if (prefixes != null) this.tablePrefixes = Arrays.asList(prefixes);
    }

    @Override
    public void removeFieldPrefixes(String... prefixes) {
        if (prefixes != null) this.fieldPrefixes = Arrays.asList(prefixes);
    }

    private String removeTablePrefixes(String name) {
        for (String prefix : tablePrefixes) {
            if (name.toLowerCase().startsWith(prefix.toLowerCase())) {
                return name.substring(prefix.length());
            }
        }
        return name;
    }

    private String removeFieldPrefixes(String name) {
        for (String prefix : fieldPrefixes) {
            if (name.toLowerCase().startsWith(prefix.toLowerCase())) {
                return name.substring(prefix.length());
            }
        }
        return name;
    }

    private String toCamelCase(String name) {
        if (StringUtils.isBlank(name)) return name;
        String[] parts = name.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            sb.append(capitalize(parts[i]));
        }
        return sb.toString();
    }

    private String toPascalCase(String name) {
        return capitalize(toCamelCase(name));
    }

    private String capitalize(String str) {
        if (StringUtils.isBlank(str)) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}