package com.ksptooi.biz.document.model.epstdword.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ksptooi.commons.dataprocess.AbstractImportDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class ImportEpStdWordDto extends AbstractImportDto {

    @ExcelProperty(value = "*中文简称")
    private String sourceName;

    @ExcelProperty(value = "中文全称")
    private String sourceNameFull;

    @ExcelProperty(value = "*英文简称")
    private String targetName;

    @ExcelProperty(value = "英文全称")
    private String targetNameFull;

    @ExcelProperty(value = "备注")
    private String remark;


    /**
     * 验证导入数据
     * @return 验证结果 如果验证通过则返回null 否则返回错误信息
     */
    @Override
    public String validate() {
        if (StringUtils.isBlank(sourceName)) {
            return "简称不能为空";
        }
        if (sourceName.length() > 128) {
            return "简称长度不能超过128个字符";
        }
        
        if (StringUtils.isNotBlank(sourceNameFull) && sourceNameFull.length() > 255) {
            return "全称长度不能超过255个字符";
        }
        
        if (StringUtils.isBlank(targetName)) {
            return "英文简称不能为空";
        }
        if (targetName.length() > 128) {
            return "英文简称长度不能超过128个字符";
        }
        
        if (StringUtils.isNotBlank(targetNameFull) && targetNameFull.length() > 128) {
            return "英文全称长度不能超过128个字符";
        }
        
        if (StringUtils.isNotBlank(remark) && remark.length() > 1000) {
            return "备注长度不能超过1000个字符";
        }
        
        return null;
    }
}

