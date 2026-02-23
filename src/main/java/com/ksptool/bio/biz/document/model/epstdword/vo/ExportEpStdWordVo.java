package com.ksptool.bio.biz.document.model.epstdword.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExportEpStdWordVo {

    @ExcelProperty(value = "标准词ID", index = 0)
    private Long id;

    @ExcelProperty(value = "简称", index = 1)
    private String sourceName;

    @ExcelProperty(value = "全称", index = 2)
    private String sourceNameFull;

    @ExcelProperty(value = "英文简称", index = 3)
    private String targetName;

    @ExcelProperty(value = "英文全称", index = 4)
    private String targetNameFull;

    @ExcelProperty(value = "备注", index = 5)
    private String remark;

}

