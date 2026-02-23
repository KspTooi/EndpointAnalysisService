package com.ksptool.bio.biz.core.model.noticercd.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AddNoticeRcdDto {


    @Schema(description = "关联通知ID")
    @NotNull(message = "关联通知ID不能为空")
    private Long noticeId;

    @Schema(description = "接收人用户ID")
    @NotNull(message = "接收人用户ID不能为空")
    private Long userId;

    @Schema(description = "读取时间 (NULL代表未读)")
    private LocalDateTime readTime;

    @Schema(description = "下发时间")
    private LocalDateTime createTime;

    @Schema(description = "删除时间 (NULL代表未删)")
    private LocalDateTime deleteTime;

}

