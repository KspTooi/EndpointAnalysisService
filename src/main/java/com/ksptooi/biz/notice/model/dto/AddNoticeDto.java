package com.ksptooi.biz.notice.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;

@Getter
@Setter
public class AddNoticeDto {


    @Schema(description="标题")
    @NotBlank(message = "标题不能为空")
    @Length(max = 32, message = "标题长度不能超过32个字符")
    private String title;

    @Schema(description="种类: 0公告, 1业务提醒, 2私信")
    @NotNull(message = "种类不能为空")
    @Range(min = 0, max = 2, message = "种类只能在0-2之间")
    private Integer kind;

    @Schema(description="通知内容")
    private String content;

    @Schema(description="优先级: 0:低 1:中 2:高")
    @NotNull(message = "优先级不能为空")
    @Range(min = 0, max = 2, message = "优先级只能在0-2之间")
    private Integer priority;

    @Schema(description="业务类型/分类")
    @Length(max = 32, message = "业务类型/分类长度不能超过32个字符")
    private String category;

    @Schema(description="发送人ID (NULL为系统)")
    private Long senderId;

    @Schema(description="发送人姓名")
    @Length(max = 32, message = "发送人姓名长度不能超过32个字符")
    private String senderName;

    @Schema(description="跳转URL/路由地址")
    @Length(max = 320, message = "跳转URL/路由地址长度不能超过320个字符")
    private String forward;

    @Schema(description="动态参数 (JSON格式)")
    private String params;

    @Schema(description="创建时间")
    private LocalDateTime createTime;

}

