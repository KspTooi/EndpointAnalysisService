package com.ksptooi.biz.core.model.noticercd.dto;

import org.hibernate.validator.constraints.Range;

import com.ksptool.assembly.entity.web.PageQuery;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserNoticeRcdListDto extends PageQuery {

    @Schema(description = "是否设置为已读: 0:否 1:是")
    @NotNull(message = "是否设置为已读不能为空")
    @Range(min = 0, max = 1, message = "是否设置为已读必须为0或1")
    private Integer setRead;

    @Schema(description = "消息标题")
    private String title;

    @Schema(description = "消息种类: 0:公告 1:业务提醒 2:私信")
    @Range(min = 0, max = 2, message = "消息种类必须为0或1或2")
    private Integer kind;

    @Schema(description = "消息正文")
    private String content;

}

