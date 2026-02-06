package com.ksptooi.biz.core.model.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class EditNoticeDto {

    @Schema(description = "主键ID")
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    @Length(max = 32, message = "标题长度不能超过32个字符")
    private String title;

    @Schema(description = "种类: 0:公告, 1:业务提醒, 2:私信")
    @NotNull(message = "种类不能为空")
    @Range(min = 0, max = 2, message = "种类只能在0-2之间")
    private Integer kind;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "优先级: 0:低 1:中 2:高")
    @NotNull(message = "优先级不能为空")
    @Range(min = 0, max = 2, message = "优先级只能在0-2之间")
    private Integer priority;

    @Schema(description = "业务类型/分类")
    @Length(max = 32, message = "业务类型/分类长度不能超过32个字符")
    private String category;

}

