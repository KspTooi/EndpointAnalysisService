package com.ksptool.bio.biz.qt.model.qttaskgroup.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetQtTaskGroupListDto extends PageQuery {

    @Schema(description = "分组名")
    private String name;

    @Schema(description = "分组备注")
    private String remark;

}

