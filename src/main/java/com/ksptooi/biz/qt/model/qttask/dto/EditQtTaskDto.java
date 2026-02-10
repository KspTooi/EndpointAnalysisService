package com.ksptooi.biz.qt.model.qttask.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import com.ksptooi.commons.dataprocess.Str;

import java.time.LocalDateTime;

@Getter
@Setter
public class EditQtTaskDto {

    @Schema(description = "任务ID")
    @NotNull(message = "任务ID不能为空")
    private Long id;

    @Schema(description = "任务分组ID")
    private Long groupId;

    @Schema(description = "任务分组名")
    @Size(max = 80, message = "任务分组名长度不能超过80")
    private String groupName;

    @Schema(description = "任务名")
    @NotBlank(message = "任务名不能为空")
    @Size(max = 80, message = "任务名长度不能超过80")
    private String name;

    @Schema(description = "0:本地BEAN 1:远程HTTP")
    @NotNull(message = "任务类型不能为空")
    @Range(min = 0, max = 1, message = "任务类型只能为0或1")
    private Integer kind;

    @Schema(description = "CRON表达式")
    @NotBlank(message = "CRON表达式不能为空")
    @Size(max = 64, message = "CRON表达式长度不能超过64")
    private String cron;

    @Schema(description = "调用目标(BEAN代码或HTTP地址)")
    @Size(max = 1000, message = "调用目标长度不能超过1000")
    private String target;

    @Schema(description = "调用参数JSON")
    private String targetParam;

    @Schema(description = "请求方法")
    @Size(max = 32, message = "请求方法长度不能超过32")
    private String reqMethod;

    @Schema(description = "并发执行 0:允许 1:禁止")
    @NotNull(message = "并发执行设置不能为空")
    @Range(min = 0, max = 1, message = "并发执行只能为0或1")
    private Integer concurrent;

    @Schema(description = "过期策略 0:放弃执行 1:立即执行 2:全部执行")
    @NotNull(message = "过期策略不能为空")
    @Range(min = 0, max = 2, message = "过期策略只能为0-2")
    private Integer misfirePolicy;

    @Schema(description = "任务有效期截止")
    private LocalDateTime expireTime;

    @Schema(description = "0:正常 1:暂停")
    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "状态只能为0或1")
    private Integer status;
    
    /**
     * 验证表单数据
     * @return 验证结果
     */
    public String validate() {

        //使用本地BEAN时不允许填写HTTP相关字段
        if (kind == 0) {

            if (Str.isNotBlank(reqMethod)) {
                return "使用本地BEAN时不允许填写请求方法";
            }

        }

        return null;
    }

}

