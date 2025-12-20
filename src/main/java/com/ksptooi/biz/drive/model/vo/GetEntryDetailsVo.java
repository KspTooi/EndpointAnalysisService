package com.ksptooi.biz.drive.model.vo;

import java.time.LocalDateTime;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetEntryDetailsVo{

    @Schema(description="条目ID")
    private Long id;

    @Schema(description="团队ID")
    private Long companyId;

    @Schema(description="父级ID 为NULL顶级")
    private Long parentId;

    @Schema(description="条目名称")
    private String name;

    @Schema(description="条目类型 0:文件 1:文件夹")
    private Integer kind;

    @Schema(description="文件附件ID 文件夹为NULL")
    private Long attachId;

    @Schema(description="文件附件大小 文件夹为0")
    private Long attachSize;

    @Schema(description="文件附件类型 文件夹为NULL")
    private String attachSuffix;

    @Schema(description="创建时间")
    private LocalDateTime createTime;

    @Schema(description="更新时间")
    private LocalDateTime updateTime;

    @Schema(description="创建人")
    private Long creatorId;

    @Schema(description="更新人")
    private Long updaterId;

}

