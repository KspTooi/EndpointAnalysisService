package com.ksptooi.biz.core.model.notice.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetNoticeListDto extends PageQuery {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "种类: 0:公告, 1:业务提醒, 2:私信")
    private Integer kind;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "优先级: 0:低 1:中 2:高")
    private Integer priority;

    @Schema(description = "业务类型/分类")
    private String category;

    @Schema(description = "发送人姓名")
    private String senderName;

}

