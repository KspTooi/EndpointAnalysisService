package com.ksptool.bio.biz.core.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户组VO
 */
@Data
public class UserGroupVo {
    /**
     * 用户组ID
     */
    @Schema(description = "用户组ID")
    private Long id;

    @Schema(description = "用户组名称")
    private String name;

    @Schema(description = "用户组描述")
    private String description;

    @Schema(description = "排序顺序")
    private Integer sortOrder;

    @Schema(description = "是否为系统内置组")
    private Boolean isSystem;

    @Schema(description = "用户是否属于此组")
    private Boolean hasGroup;

    @Schema(description = "组状态 0:禁用，1:启用")
    private Integer status;

} 