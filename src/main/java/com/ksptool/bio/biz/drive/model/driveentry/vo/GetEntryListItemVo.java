package com.ksptool.bio.biz.drive.model.driveentry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetEntryListItemVo {

    @Schema(description = "条目ID")
    private Long id;

    @Schema(description = "条目名称")
    private String name;

    @Schema(description = "条目类型 0:文件 1:文件夹")
    private Integer kind;

    @Schema(description = "文件附件ID 文件夹为NULL")
    private Long attachId;

    @Schema(description = "文件附件大小 文件夹为0")
    private Long attachSize;

    @Schema(description = "文件附件类型 文件夹为NULL")
    private String attachSuffix;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
