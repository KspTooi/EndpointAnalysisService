package com.ksptooi.biz.noticercd.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Getter
@Setter
public class GetNoticeRcdListVo{

    @Schema(description="主键ID")
    private Long id;

    @Schema(description="关联通知ID")
    private Long noticeId;

    @Schema(description="接收人用户ID")
    private Long userId;

    @Schema(description="读取时间 (NULL代表未读)")
    private LocalDateTime readTime;

    @Schema(description="下发时间")
    private LocalDateTime createTime;

    @Schema(description="删除时间 (NULL代表未删)")
    private LocalDateTime deleteTime;

}

