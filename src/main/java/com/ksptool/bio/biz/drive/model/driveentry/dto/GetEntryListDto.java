package com.ksptool.bio.biz.drive.model.driveentry.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class GetEntryListDto extends PageQuery {

    @Schema(description = "空间ID")
    @NotNull(message = "空间ID不能为空")
    private Long driveSpaceId;

    @Schema(description = "目录ID NULL为根目录")
    private Long directoryId;

    @Schema(description = "关键字查询")
    private String keyword;

}

