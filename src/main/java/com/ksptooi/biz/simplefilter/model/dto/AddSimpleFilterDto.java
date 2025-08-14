package com.ksptooi.biz.simplefilter.model.dto;

import java.util.Date;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class AddSimpleFilterDto {

    @Schema(description="过滤器名称")
    private String name;

    @Schema(description="过滤器方向 0:请求过滤器 1:响应过滤器")
    private Integer direction;

    @Schema(description="触发条件 0:标头包含 1:标头不包含 2:JSON载荷包含 3:JSON载荷不包含 10:总是触发")
    private Integer triggerCondition;

    @Schema(description="触发键")
    private String triggerKey;

    @Schema(description="触发值")
    private String triggerValue;

    @Schema(description="过滤器操作 0:获取头 1:获取JSON 2:注入头 3:注入JSON 4:更改请求业务状态")
    private Integer operation;

    @Schema(description="获取键")
    private String getKey;

    @Schema(description="获取值")
    private String getValue;

    @Schema(description="注入方式 0:注入字面量 1:从配置表获取")
    private Integer injectType;

    @Schema(description="注入键")
    private String injectKey;

    @Schema(description="注入值 当注入方式为0时 注入字面量 当注入方式为1时 注入配置表中的值")
    private String injectValue;

    @Schema(description="注入状态码 0:正常 1:HTTP失败 2:业务失败 3:连接超时")
    private Integer injectStatusCode;

    @Schema(description="状态 0:启用 1:禁用")
    private Integer status;

}

