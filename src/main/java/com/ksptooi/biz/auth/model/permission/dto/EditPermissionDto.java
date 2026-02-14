package com.ksptooi.biz.auth.model.permission.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class EditPermissionDto {
    /**
     * 权限ID
     */
    @NotNull(message = "权限ID不能为空")
    @Min(value = 1, message = "权限ID必须大于0")
    private Long id;

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    @Size(min = 1, max = 32, message = "权限名称长度必须在1到32个字符之间")
    private String name;

    /**
     * 权限代码
     */
    @NotBlank(message = "权限标识不能为空")
    @Size(min = 1, max = 320, message = "权限标识长度必须在1到320个字符之间")
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
    @Min(value = 0, message = "排序号不能小于0")
    private Integer seq;

    /**
     * 系统内置权限 0:否 1:是
     */
    @NotNull(message = "系统内置权限标识不能为空")
    @Range(min = 0, max = 1, message = "系统内置权限标识必须为0或1")
    private Integer isSystem;
}
