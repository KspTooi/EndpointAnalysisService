package com.ksptool.bio.biz.core.model.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class AddNoticeDto {


    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    @Length(max = 32, message = "标题长度不能超过32个字符")
    private String title;

    @Schema(description = "种类: 0公告, 1业务提醒, 2私信")
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

    @Schema(description = "接收对象类型 0:全员 1:指定部门 2:指定用户")
    @NotNull(message = "接收对象类型不能为空")
    @Range(min = 0, max = 2, message = "接收对象类型只能在0-2之间")
    private Integer targetKind;

    @Schema(description = "接收对象ID列表 当targetKind为1、2时必填")
    private List<Long> targetIds;

    /**
     * 验证参数
     *
     * @return 错误信息 当参数合法时返回null
     */
    public String validate() {
        if (targetKind == 1 || targetKind == 2) {
            if (targetIds == null || targetIds.isEmpty()) {
                return "当接收对象类型为指定部门或指定用户时，必须选择一个合法的接收对象";
            }
        }

        //最多选择500个部门
        if (targetKind == 1) {
            if (targetIds.size() > 500) {
                return "最多选择500个部门";
            }
        }

        //最多选择5000个用户
        if (targetKind == 2) {
            if (targetIds.size() > 5000) {
                return "最多选择5000个用户";
            }
        }

        return null;
    }

}

