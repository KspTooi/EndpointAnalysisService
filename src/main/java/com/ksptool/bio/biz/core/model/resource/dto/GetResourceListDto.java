package com.ksptool.bio.biz.core.model.resource.dto;


import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetResourceListDto extends PageQuery {

    @Schema(description = "资源名")
    private String name;

    @Schema(description = "资源描述")
    private String description;

    @Schema(description = "资源类型 0:菜单 1:接口")
    private Integer kind;

    @Schema(description = "菜单类型 0:目录 1:菜单 1:按钮")
    private Integer menuKind;

    @Schema(description = "菜单路径")
    private String menuPath;

    @Schema(description = "接口路径")
    private String path;

    @Schema(description = "所需权限")
    private String permission;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updaterId;

}

