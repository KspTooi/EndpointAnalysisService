package com.ksptool.bio.commons.dataprocess.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * IntegerRow 注解专用转换器
 * 处理 Integer 类型的 KV 映射与必填校验
 */
public class IntegerRowConverter implements Converter<Integer> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 读数据：Excel (String) -> Java (Integer)
     */
    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        // 1. 获取 Excel 单元格的文本值
        String excelValue = cellData.getStringValue();

        // 2. 获取字段上的注解
        IntegerRow annotation = contentProperty.getField().getAnnotation(IntegerRow.class);

        // 如果没有注解，尝试直接转数字，转不了就抛错或返回null
        if (annotation == null) {
            return StringUtils.isNotBlank(excelValue) ? Integer.parseInt(excelValue) : null;
        }

        // 3. 处理空值与必填校验
        if (StringUtils.isEmpty(excelValue)) {
            if (annotation.required()) {
                String errorMsg = StringUtils.isEmpty(annotation.message())
                        ? "该字段不能为空"
                        : annotation.message();
                // 抛出 ExcelAnalysisException，方便在 Listener 中捕获
                throw new ExcelAnalysisException(errorMsg);
            }
            return null; // 非必填且为空，返回 null
        }

        // 4. 解析 KV 映射规则 (格式: "0=男;1=女")
        // key=中文描述, value=Integer代码
        Map<String, Integer> map = parseFormatToMapForRead(annotation.format());

        // 5. 尝试匹配
        // 先去空格
        String key = excelValue.trim();
        Integer result = map.get(key);

        if (result != null) {
            return result;
        }

        // 6. 如果没匹配上，但内容本身就是数字 (例如用户直接填了 '1')，尝试直接转换
        try {
            return Integer.parseInt(key);
        } catch (NumberFormatException e) {
            // 如果既匹配不上映射，也不是数字，抛出异常
            throw new ExcelAnalysisException("解析失败，值【" + key + "】不在允许的映射范围内: " + annotation.format());
        }
    }

    /**
     * 写数据：Java (Integer) -> Excel (String)
     */
    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == null) {
            return new WriteCellData<>("");
        }

        IntegerRow annotation = contentProperty.getField().getAnnotation(IntegerRow.class);
        if (annotation == null || StringUtils.isEmpty(annotation.format())) {
            return new WriteCellData<>(String.valueOf(value));
        }

        // 解析映射规则 (反向: code -> 中文)
        Map<Integer, String> map = parseFormatToMapForWrite(annotation.format());

        String showName = map.get(value);
        if (showName != null) {
            return new WriteCellData<>(showName);
        }

        // 如果没找到映射，原样输出数字
        return new WriteCellData<>(String.valueOf(value));
    }

    // --- 内部工具方法 ---

    /**
     * 解析 format 字符串用于【读取】
     * Input: "0=男;1=女"
     * Output: Map<"男", 0>, Map<"女", 1>
     */
    private Map<String, Integer> parseFormatToMapForRead(String format) {
        Map<String, Integer> map = new HashMap<>();
        if (StringUtils.isEmpty(format)) return map;

        String[] entries = format.split(";");
        for (String entry : entries) {
            String[] kv = entry.split("=");
            if (kv.length == 2) {
                try {
                    Integer code = Integer.parseInt(kv[0].trim());
                    String text = kv[1].trim();
                    map.put(text, code);
                } catch (NumberFormatException e) {
                    // 忽略格式错误的配置
                }
            }
        }
        return map;
    }

    /**
     * 解析 format 字符串用于【写入】
     * Input: "0=男;1=女"
     * Output: Map<0, "男">, Map<1, "女">
     */
    private Map<Integer, String> parseFormatToMapForWrite(String format) {
        Map<Integer, String> map = new HashMap<>();
        if (StringUtils.isEmpty(format)) return map;

        String[] entries = format.split(";");
        for (String entry : entries) {
            String[] kv = entry.split("=");
            if (kv.length == 2) {
                try {
                    Integer code = Integer.parseInt(kv[0].trim());
                    String text = kv[1].trim();
                    map.put(code, text);
                } catch (NumberFormatException e) {
                    // 忽略
                }
            }
        }
        return map;
    }
}