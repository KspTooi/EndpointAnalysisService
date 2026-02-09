package com.ksptooi.biz.core.model.registry.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ksptooi.commons.dataprocess.AbstractImportDto;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ExportRegistryVo extends AbstractImportDto {

    @ExcelProperty(value = "*条目Key")
    private String nkey;

    @ExcelProperty(value = "*数据类型")
    private String nvalueKind;

    @ExcelProperty(value = "*条目Value")
    private String nvalue;

    @ExcelProperty(value = "条目标签")
    private String label;

    @ExcelProperty(value = "备注")
    private String remark;

    @ExcelProperty(value = "元数据JSON")
    private String metadata;

    @ExcelProperty(value = "*状态")
    private String status;

    @ExcelProperty(value = "*排序")
    private String seq;

    /**
     * 验证接口参数
     *
     * @return 错误信息 无错误返回null
     */
    public String validate() {
        
        return null;
    }


}
