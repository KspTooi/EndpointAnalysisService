package com.ksptooi.biz.auth.model.permission.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavePermissionDto {
    /**
     * 权限ID，创建时为空
     */
    private Long id;

    /**
     * 权限名称
     */
    @NotEmpty(message = "权限名称不能为空")
    @Size(max = 32, message = "权限名称长度不能超过32个字符")
    private String name;

    /**
     * 权限代码
     */
    @NotEmpty(message = "权限标识不能为空")
    @Size(max = 320, message = "权限标识长度不能超过320个字符")
    @Pattern(regexp = "^[a-z]([a-z0-9\\-]*[a-z0-9])*(:[a-z]([a-z0-9\\-]*[a-z0-9])*)*$",
            message = "权限标识格式错误，只允许小写字母、数字、连字符，以及冒号作为分隔符")
    private String code;

    /**
     * 权限描述
     */
    private String remark;

    /**
     * 排序号
     */
    @NotNull(message = "排序号不能为空")
    private Integer seq;

    /**
     * 系统内置权限 0:否 1:是
     */
    @NotNull(message = "系统内置权限标识不能为空")
    private Integer isSystem;
}
