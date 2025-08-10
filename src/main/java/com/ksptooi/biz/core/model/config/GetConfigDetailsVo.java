package com.ksptooi.biz.core.model.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter@Setter
@Schema(name = "GetConfigDetailsVo")
public class GetConfigDetailsVo {

    // 主键ID
    private Long id;

    // 用户ID
    private Long userId;

    // 人物名称
    private String playerName;

    // 配置键
    private String configKey;

    // 配置值
    private String configValue;

    // 配置描述
    private String description;

    // 创建时间
    private Date createTime;

    // 更新时间
    private Date updateTime;

}
