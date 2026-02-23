package com.ksptool.bio.commons.dataprocess.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.StringUtils;

public class StringRowConverter implements Converter<String> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String excelValue = cellData.getStringValue();
        if (excelValue != null) {
            excelValue = excelValue.trim();
        }

        StringRow annotation = contentProperty.getField().getAnnotation(StringRow.class);
        if (annotation == null) {
            return excelValue;
        }

        // --- 修复部分 开始 ---
        // 原代码可能报错: contentProperty.getHead().getHeadNameList().get(0);
        // 改为获取 Java 字段名，兼容性更好
        String fieldName = contentProperty.getField().getName();
        // --- 修复部分 结束 ---

        if (StringUtils.isEmpty(excelValue)) {
            if (annotation.required()) {
                throw new ExcelAnalysisException(formatError(fieldName, "不能为空", annotation.message()));
            }
            return null;
        }

        int length = excelValue.length();

        if (annotation.max() > 0 && length > annotation.max()) {
            throw new ExcelAnalysisException(
                    formatError(fieldName, "长度不能超过 " + annotation.max() + " 位 (当前 " + length + ")", annotation.message())
            );
        }

        if (annotation.min() > 0 && length < annotation.min()) {
            throw new ExcelAnalysisException(
                    formatError(fieldName, "长度不能少于 " + annotation.min() + " 位 (当前 " + length + ")", annotation.message())
            );
        }

        return excelValue;
    }

    @Override
    public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(value == null ? "" : value);
    }

    private String formatError(String fieldName, String defaultMsg, String customMsg) {
        if (StringUtils.isNotBlank(customMsg)) {
            return customMsg;
        }
        // 提示信息会显示：字段【name】不能为空
        return String.format("字段【%s】%s", fieldName, defaultMsg);
    }
}