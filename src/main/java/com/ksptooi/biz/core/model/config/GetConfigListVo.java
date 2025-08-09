package com.ksptooi.biz.core.model.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter@Setter
public class GetConfigListVo {

    // 主键ID
    private Long id;

    // 用户名
    private String userName;

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

    // 无参构造函数
    public GetConfigListVo() {
    }


    public GetConfigListVo(Long id, String userName, String configKey, String configValue, String description, Date createTime, Date updateTime) {
        this.id = id;
        this.userName = userName;
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}