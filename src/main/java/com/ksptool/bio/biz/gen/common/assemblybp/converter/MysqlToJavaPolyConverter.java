package com.ksptool.bio.biz.gen.common.assemblybp.converter;

import com.ksptool.bio.biz.gen.common.assemblybp.entity.blueprint.PolyBlueprint;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.blueprint.RawBlueprint;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.field.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class MysqlToJavaPolyConverter implements PolyConverter {

    //MySQL类型到Java类型的映射
    public static final Map<String, JavaTypeInfo> TYPE_MAP = new HashMap<>();
    //日志
    private static final Logger logger = LoggerFactory.getLogger(MysqlToJavaPolyConverter.class);

    static {
        TYPE_MAP.put("VARCHAR", JavaTypeInfo.of(String.class));
        TYPE_MAP.put("CHAR", JavaTypeInfo.of(String.class));
        TYPE_MAP.put("TEXT", JavaTypeInfo.of(String.class));
        TYPE_MAP.put("TINYTEXT", JavaTypeInfo.of(String.class));
        TYPE_MAP.put("MEDIUMTEXT", JavaTypeInfo.of(String.class));
        TYPE_MAP.put("LONGTEXT", JavaTypeInfo.of(String.class));
        TYPE_MAP.put("INT", JavaTypeInfo.of(Integer.class));
        TYPE_MAP.put("INTEGER", JavaTypeInfo.of(Integer.class));
        TYPE_MAP.put("BIGINT", JavaTypeInfo.of(Long.class));
        TYPE_MAP.put("DECIMAL", JavaTypeInfo.of(BigDecimal.class));
        TYPE_MAP.put("NUMERIC", JavaTypeInfo.of(BigDecimal.class));
        TYPE_MAP.put("FLOAT", JavaTypeInfo.of(Float.class));
        TYPE_MAP.put("DOUBLE", JavaTypeInfo.of(Double.class));
        TYPE_MAP.put("BOOLEAN", JavaTypeInfo.of(Boolean.class));
        TYPE_MAP.put("TINYINT", JavaTypeInfo.of(Integer.class));
        TYPE_MAP.put("SMALLINT", JavaTypeInfo.of(Integer.class));
        TYPE_MAP.put("MEDIUMINT", JavaTypeInfo.of(Integer.class));
        TYPE_MAP.put("DATE", JavaTypeInfo.of(LocalDate.class));
        TYPE_MAP.put("DATETIME", JavaTypeInfo.of(LocalDateTime.class));
        TYPE_MAP.put("TIMESTAMP", JavaTypeInfo.of(LocalDateTime.class));
        TYPE_MAP.put("TIME", JavaTypeInfo.of(LocalTime.class));
        TYPE_MAP.put("BLOB", new JavaTypeInfo("byte[]", "byte[]", null));
        TYPE_MAP.put("BINARY", new JavaTypeInfo("byte[]", "byte[]", null));
        TYPE_MAP.put("VARBINARY", new JavaTypeInfo("byte[]", "byte[]", null));
        TYPE_MAP.put("JSON", JavaTypeInfo.of(String.class));
    }

    //表名前缀列表
    private List<String> tablePrefixes = new ArrayList<>();
    //字段名前缀列表
    private List<String> fieldPrefixes = new ArrayList<>();

    @Override
    public PolyTable toPolyTable(RawTable rawTable) {
        if (rawTable == null) {
            logger.warn("原始表为null，无法转换");
            return null;
        }
        logger.debug("转换表：{}", rawTable.getName());
        PolyTable polyTable = new PolyTable();
        polyTable.setRawTable(rawTable);
        String javaClassName = toJavaClassName(rawTable.getName());
        polyTable.setStdName(javaClassName);
        polyTable.setComment(rawTable.getComment());
        polyTable.setSeq(rawTable.getSeq());

        List<PolyField> polyFields = new ArrayList<>();
        Set<String> importSet = new HashSet<>();
        if (rawTable.getFields() != null) {
            for (RawField rawField : rawTable.getFields()) {
                PolyField polyField = toPolyField(rawField);
                if (polyField == null) {
                    continue;
                }
                polyFields.add(polyField);
                String fullTypeName = getFullTypeName(rawField.getType());
                if (fullTypeName != null && !isPrimitiveType(fullTypeName) && !isArrayType(fullTypeName)) {
                    importSet.add(fullTypeName);
                }
            }
        }
        polyTable.setFields(polyFields);

        List<PolyImport> imports = new ArrayList<>();
        List<String> sortedImports = new ArrayList<>(importSet);
        sortedImports.sort(String::compareTo);
        for (String importPath : sortedImports) {
            PolyImport polyImport = new PolyImport();
            polyImport.setPath(importPath);
            imports.add(polyImport);
        }
        polyTable.setImports(imports);
        logger.info("表{}转换完成，字段数：{}，导入数：{}", rawTable.getName(), polyFields.size(), imports.size());
        return polyTable;
    }

    @Override
    public PolyField toPolyField(RawField rawField) {
        if (rawField == null) {
            logger.warn("原始字段为null，无法转换");
            return null;
        }
        PolyField polyField = new PolyField();
        polyField.setRawField(rawField);
        String javaFieldName = toJavaFieldName(rawField.getName());
        String javaStdName = capitalize(javaFieldName);
        polyField.setStdName(javaStdName);
        polyField.setType(toJavaType(rawField.getType()));
        polyField.setComment(rawField.getComment());
        polyField.setRequired(rawField.isRequired());
        polyField.setPrimaryKey(rawField.isPrimaryKey());
        polyField.setSeq(rawField.getSeq());
        logger.debug("字段{}转换为{}，类型：{}", rawField.getName(), polyField.getStdName(), polyField.getType());
        return polyField;
    }

    @Override
    public void removeTablePrefixes(String... prefixes) {
        if (prefixes == null || prefixes.length == 0) {
            this.tablePrefixes.clear();
            return;
        }
        this.tablePrefixes = new ArrayList<>(Arrays.asList(prefixes));
        logger.debug("设置表名前缀：{}", tablePrefixes);
    }

    @Override
    public void removeFieldPrefixes(String... prefixes) {
        if (prefixes == null || prefixes.length == 0) {
            this.fieldPrefixes.clear();
            return;
        }
        this.fieldPrefixes = new ArrayList<>(Arrays.asList(prefixes));
        logger.debug("设置字段名前缀：{}", fieldPrefixes);
    }

    private String toJavaClassName(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return tableName;
        }
        String processedName = removeTablePrefixes(tableName);
        String camelCase = toCamelCase(processedName);
        return capitalize(camelCase);
    }

    private String removeTablePrefixes(String tableName) {
        if (StringUtils.isBlank(tableName) || tablePrefixes == null || tablePrefixes.isEmpty()) {
            return tableName;
        }
        for (String prefix : tablePrefixes) {
            if (StringUtils.isNotBlank(prefix) && tableName.toLowerCase().startsWith(prefix.toLowerCase())) {
                String removed = tableName.substring(prefix.length());
                logger.debug("去除表名前缀：{} -> {}", prefix, removed);
                return removed;
            }
        }
        return tableName;
    }

    private String toJavaFieldName(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return fieldName;
        }
        String processedName = removeFieldPrefixes(fieldName);
        return toCamelCase(processedName);
    }

    private String removeFieldPrefixes(String fieldName) {
        if (StringUtils.isBlank(fieldName) || fieldPrefixes == null || fieldPrefixes.isEmpty()) {
            return fieldName;
        }
        for (String prefix : fieldPrefixes) {
            if (StringUtils.isNotBlank(prefix) && fieldName.toLowerCase().startsWith(prefix.toLowerCase())) {
                String removed = fieldName.substring(prefix.length());
                logger.debug("去除字段名前缀：{} -> {}", prefix, removed);
                return removed;
            }
        }
        return fieldName;
    }

    private String toCamelCase(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }
        String[] parts = name.toLowerCase().split("_");
        if (parts.length == 0) {
            return name;
        }
        StringBuilder result = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            if (StringUtils.isNotBlank(parts[i])) {
                result.append(capitalize(parts[i]));
            }
        }
        return result.toString();
    }

    private String capitalize(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String toJavaType(String mysqlType) {
        if (StringUtils.isBlank(mysqlType)) {
            return "String";
        }
        String type = mysqlType.toUpperCase().trim();
        int parenIndex = type.indexOf('(');
        String baseType = parenIndex > 0 ? type.substring(0, parenIndex) : type;
        JavaTypeInfo typeInfo = TYPE_MAP.get(baseType);
        if (typeInfo != null) {
            if ("TINYINT".equals(baseType) && type.contains("1")) {
                return "Boolean";
            }
            return typeInfo.getSimpleName();
        }
        logger.warn("未知的MySQL类型：{}，默认使用String", mysqlType);
        return "String";
    }

    private String getFullTypeName(String mysqlType) {
        if (StringUtils.isBlank(mysqlType)) {
            return String.class.getName();
        }
        String type = mysqlType.toUpperCase().trim();
        int parenIndex = type.indexOf('(');
        String baseType = parenIndex > 0 ? type.substring(0, parenIndex) : type;
        JavaTypeInfo typeInfo = TYPE_MAP.get(baseType);
        if (typeInfo != null) {
            if ("TINYINT".equals(baseType) && type.contains("1")) {
                return Boolean.class.getName();
            }
            return typeInfo.getFullName();
        }
        return String.class.getName();
    }

    private boolean isPrimitiveType(String fullTypeName) {
        if (StringUtils.isBlank(fullTypeName)) {
            return true;
        }
        return fullTypeName.startsWith("java.lang.");
    }

    private boolean isArrayType(String fullTypeName) {
        if (StringUtils.isBlank(fullTypeName)) {
            return false;
        }
        return "byte[]".equals(fullTypeName) || fullTypeName.endsWith("[]");
    }

    @Override
    public PolyBlueprint toPolyBlueprint(RawBlueprint rawBlueprint) {
        if (rawBlueprint == null) {
            logger.warn("原始蓝图为null，无法转换");
            return null;
        }

        try {
            Path filePath = rawBlueprint.getFilePath();
            if (!Files.exists(filePath)) {
                logger.warn("蓝图文件不存在：{}", filePath);
                return null;
            }

            String templateContent = Files.readString(filePath);
            String relativeFilePath = rawBlueprint.getRelativeFilePath();
            String fileName = rawBlueprint.getFileName();

            PolyBlueprint polyBlueprint = new PolyBlueprint();
            polyBlueprint.setRawBlueprint(rawBlueprint);
            polyBlueprint.setTemplateContent(templateContent);
            polyBlueprint.setRelativePathWithPlaceholder(relativeFilePath);
            polyBlueprint.setFileNameWithPlaceholder(fileName);

            logger.debug("转换蓝图：{}", relativeFilePath);
            return polyBlueprint;
        } catch (IOException e) {
            logger.error("读取蓝图文件失败：{}", rawBlueprint.getAbsoluteFilePath(), e);
            return null;
        }
    }

}

