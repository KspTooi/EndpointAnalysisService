package com.ksptool.bio.biz.gen.model.datsource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddDataSourceDto {

    @NotBlank(message = "数据源名称不能为空")
    @Size(max = 32, min = 1, message = "数据源名称长度必须在1-32个字符之间")
    @Schema(description = "数据源名称")
    private String name;

    @NotBlank(message = "数据源编码不能为空")
    @Size(max = 32, min = 1, message = "数据源编码长度必须在1-32个字符之间")
    @Schema(description = "数据源编码")
    private String code;

    @NotNull(message = "数据源类型不能为空")
    @Schema(description = "数据源类型 0:MYSQL")
    private Integer kind;

    @NotBlank(message = "JDBC驱动不能为空")
    @Size(max = 80, min = 1, message = "JDBC驱动长度必须在1-80个字符之间")
    @Schema(description = "JDBC驱动")
    private String drive;

    @Size(max = 1000, message = "连接字符串长度不能超过1000个字符")
    @NotBlank(message = "连接字符串不能为空")
    @Schema(description = "连接字符串")
    private String url;

    @Size(max = 320, message = "连接用户名长度不能超过320个字符")
    @Schema(description = "连接用户名")
    private String username;

    @Size(max = 1280, message = "连接密码长度不能超过1280个字符")
    @Schema(description = "连接密码")
    private String password;

    @NotBlank(message = "默认模式不能为空")
    @Size(max = 80, min = 1, message = "默认模式长度必须在1-80个字符之间")
    @Schema(description = "默认模式")
    private String dbSchema;

}
