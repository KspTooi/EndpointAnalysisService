package com.ksptool.bio.biz.document.model.epsite.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExportEpSiteVo {

    @ExcelProperty(value = "站点ID", index = 0)
    private Long id;

    @ExcelProperty(value = "站点名称", index = 1)
    private String name;

    @ExcelProperty(value = "地址", index = 2)
    private String address;

    @ExcelProperty(value = "账户", index = 3)
    private String username;

    @ExcelProperty(value = "密码", index = 4)
    private String password;

    @ExcelProperty(value = "备注", index = 5)
    private String remark;

    @ExcelProperty(value = "排序", index = 6)
    private Integer seq;

}
