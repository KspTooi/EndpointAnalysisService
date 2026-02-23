package com.ksptool.bio.biz.relay.model.routeserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class AddRouteServerDto {

    @Length(max = 32, message = "服务器名称长度不能超过32个字符")
    @NotBlank(message = "服务器名称不能为空")
    @Schema(description = "服务器名称")
    private String name;

    @Length(max = 32, message = "服务器主机长度不能超过32个字符")
    @NotBlank(message = "服务器主机不能为空")
    @Schema(description = "服务器主机")
    private String host;

    @Range(min = 1, max = 65535, message = "服务器端口必须在1-65535之间")
    @NotNull(message = "服务器端口不能为空")
    @Schema(description = "服务器端口")
    private Integer port;

    @Length(max = 5000, message = "备注长度不能超过5000个字符")
    @Schema(description = "备注")
    private String remark;

    @Range(min = 0, max = 1, message = "服务器状态只能在0或1之间")
    @NotNull(message = "服务器状态不能为空")
    @Schema(description = "服务器状态 0:禁用 1:启用")
    private Integer status;


}

