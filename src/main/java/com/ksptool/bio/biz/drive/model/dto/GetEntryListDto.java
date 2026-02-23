package com.ksptool.bio.biz.drive.model.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEntryListDto extends PageQuery {

    @Schema(description = "目录ID NULL为根目录")
    private Long directoryId;

    @Schema(description = "关键字查询")
    private String keyword;

}

