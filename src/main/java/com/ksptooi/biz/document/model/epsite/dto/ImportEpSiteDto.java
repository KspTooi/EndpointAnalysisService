package com.ksptooi.biz.document.model.epsite.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ksptool.bio.commons.dataprocess.AbstractImportDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class ImportEpSiteDto extends AbstractImportDto {

    @ExcelProperty(value = "*站点名称")
    private String name;

    @ExcelProperty(value = "地址")
    private String address;

    @ExcelProperty(value = "账户")
    private String username;

    @ExcelProperty(value = "密码")
    private String password;

    @ExcelProperty(value = "备注")
    private String remark;

    @ExcelProperty(value = "*排序")
    private Integer seq;

    /**
     * 验证导入数据
     *
     * @return 验证结果 如果验证通过则返回null 否则返回错误信息
     */
    @Override
    public String validate() {
        if (StringUtils.isBlank(name)) {
            return "站点名称不能为空";
        }
        if (name.length() > 32) {
            return "站点名称长度不能超过32个字符";
        }

        if (StringUtils.isNotBlank(address) && address.length() > 255) {
            return "地址长度不能超过255个字符";
        }

        if (StringUtils.isNotBlank(username) && username.length() > 500) {
            return "账户长度不能超过500个字符";
        }

        if (StringUtils.isNotBlank(password) && password.length() > 500) {
            return "密码长度不能超过500个字符";
        }

        if (StringUtils.isNotBlank(remark) && remark.length() > 1000) {
            return "备注长度不能超过1000个字符";
        }

        if (seq == null) {
            return "排序不能为空";
        }

        return null;
    }
}
