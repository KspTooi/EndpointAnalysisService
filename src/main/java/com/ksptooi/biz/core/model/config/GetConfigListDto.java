package com.ksptooi.biz.core.model.config;


import com.ksptooi.commons.utils.page.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetConfigListDto extends PageQuery {

    // 配置键/值/描述
    private String keyword;

    // 所有者
    private String playerName;

} 