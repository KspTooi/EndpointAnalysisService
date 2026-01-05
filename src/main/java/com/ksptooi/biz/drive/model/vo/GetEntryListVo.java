package com.ksptooi.biz.drive.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class GetEntryListVo {

    @Schema(description = "目录ID")
    private Long dirId;

    @Schema(description = "目录名称")
    private String dirName;

    @Schema(description = "父级目录ID 为NULL顶级")
    private Long dirParentId;

    @Schema(description = "条目列表")
    private List<GetEntryListItemVo> items;

    @Schema(description = "条目总数")
    private Long total;

    @Schema(description = "当前目录路径(至多10层)")
    private List<GetEntryListPathVo> paths;

}

