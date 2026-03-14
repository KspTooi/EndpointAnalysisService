package com.ksptool.bio.biz.assembly.model.datsource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetDataSourceListVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "数据源名称")
    private String name;

    @Schema(description = "数据源编码")
    private String code;

    @Schema(description = "数据源类型 0:MYSQL")
    private Integer kind;

    @Schema(description = "连接字符串")
    private String url;

    @Schema(description = "默认模式")
    private String dbSchema;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

