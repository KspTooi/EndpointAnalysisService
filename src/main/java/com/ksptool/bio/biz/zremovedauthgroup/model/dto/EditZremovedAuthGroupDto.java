package com.ksptool.bio.biz.zremovedauthgroup.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Getter
@Setter
public class EditZremovedAuthGroupDto {


    @Schema(description="组ID")
    private Long id;

    @Schema(description="组标识，如：admin、developer等")
    private String code;

    @Schema(description="组名称，如：管理员组、开发者组等")
    private String name;

    @Schema(description="组描述")
    private String remark;

    @Schema(description="组状态:0:禁用，1:启用")
    private Integer status;

    @Schema(description="排序号")
    private Integer seq;

    @Schema(description="系统内置组 0:否 1:是")
    private Integer isSystem;

    @Schema(description="创建时间")
    private LocalDateTime createTime;

    @Schema(description="创建人ID")
    private Long creatorId;

    @Schema(description="修改时间")
    private LocalDateTime updateTime;

    @Schema(description="修改人ID")
    private Long updaterId;

}

