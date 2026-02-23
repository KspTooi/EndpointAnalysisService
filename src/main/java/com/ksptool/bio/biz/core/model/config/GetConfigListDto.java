package com.ksptool.bio.biz.core.model.config;


import com.ksptool.assembly.entity.web.PageQuery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetConfigListDto extends PageQuery {

    // 配置键/值/描述
    @Schema(description = "配置键/值/描述")
    private String keyword;

    // 所有者
    @Schema(description = "所有者")
    private String userName;

} 