package assembly;

import com.ksptool.assembly.blueprint.converter.PolyConverter;
import com.ksptool.assembly.blueprint.entity.blueprint.PolyBlueprint;
import com.ksptool.assembly.blueprint.entity.blueprint.RawBlueprint;
import com.ksptool.assembly.blueprint.entity.field.PolyField;
import com.ksptool.assembly.blueprint.entity.field.PolyTable;
import com.ksptool.assembly.blueprint.entity.field.RawField;
import com.ksptool.assembly.blueprint.entity.field.RawTable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * MySQL 到 TypeScript 的聚合转换器
 */
public class MysqlToTypeScriptPolyConverter implements PolyConverter {

    private static final Logger logger = LoggerFactory.getLogger(MysqlToTypeScriptPolyConverter.class);

    // MySQL类型到TS类型的映射
    public static final Map<String, String> TYPE_MAP = new HashMap<>();

    private List<String> tablePrefixes = new ArrayList<>();
    private List<String> fieldPrefixes = new ArrayList<>();

    static {
        // 字符串类型
        TYPE_MAP.put("VARCHAR", "string");
        TYPE_MAP.put("CHAR", "string");
        TYPE_MAP.put("TEXT", "string");
        TYPE_MAP.put("LONGTEXT", "string");
        TYPE_MAP.put("JSON", "string");

        // 数字类型
        TYPE_MAP.put("INT", "number");
        TYPE_MAP.put("INTEGER", "number");
        TYPE_MAP.put("BIGINT", "string");
        TYPE_MAP.put("DECIMAL", "number");
        TYPE_MAP.put("DOUBLE", "number");
        TYPE_MAP.put("FLOAT", "number");
        TYPE_MAP.put("TINYINT", "number");

        // 布尔类型
        TYPE_MAP.put("BIT", "boolean");
        TYPE_MAP.put("BOOLEAN", "boolean");

        // 时间类型 (前端通常处理为字符串)
        TYPE_MAP.put("DATE", "string");
        TYPE_MAP.put("DATETIME", "string");
        TYPE_MAP.put("TIMESTAMP", "string");
        TYPE_MAP.put("TIME", "string");
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
        List<PolyField> polyFields = new ArrayList<>();
        if (rawTable.getFields() != null) {
            for (RawField rawField : rawTable.getFields()) {
                polyFields.add(toPolyField(rawField));
            }
        }
        polyTable.setFields(polyFields);

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
        polyField.setType(toTsType(rawField.getType()));

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

    // --- 辅助方法 ---

    private String toTsType(String mysqlType) {
        if (StringUtils.isBlank(mysqlType)) return "any";
        String type = mysqlType.toUpperCase().trim();
        // 提取基本类型 (去除长度等，如 VARCHAR(32) -> VARCHAR)
        String baseType = type.split("\\(")[0];

        // 特殊处理 TINYINT(1) -> boolean
        if ("TINYINT".equals(baseType) && type.contains("(1)")) {
            return "number"; // 你的模板中状态是0/1，所以这里保持number，如果是boolean字段可改为boolean
        }

        return TYPE_MAP.getOrDefault(baseType, "any");
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