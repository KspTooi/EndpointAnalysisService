package com.ksptool.bio.biz.gen.model.datsource.vo;

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

    @Schema(description = "JDBC驱动")
    private String drive;

    @Schema(description = "连接字符串")
    private String url;

    @Schema(description = "连接用户名")
    private String username;

    @Schema(description = "连接密码")
    private String password;

    @Schema(description = "默认模式")
    private String dbSchema;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updaterId;

}

