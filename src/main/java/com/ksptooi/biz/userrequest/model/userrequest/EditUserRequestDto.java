package com.ksptooi.biz.userrequest.model.userrequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter@Setter
public class EditUserRequestDto {

    @NotNull(message = "用户请求ID不能为空")
    @Schema(description="用户请求ID")
    private Long id;

    @NotBlank(message = "用户自定义请求名称不能为空")
    @Schema(description="用户自定义请求名称")
    private String name;

    @NotBlank(message = "请求方法不能为空")
    @Schema(description="请求方法")
    private String method;

    @NotBlank(message = "请求URL不能为空")
    @Schema(description="请求URL")
    private String url;

    @NotNull(message = "请求头不能为空")
    @Schema(description="请求头")
    private Map<String,String> requestHeaders;

    @NotBlank(message = "请求体类型不能为空")
    @Schema(description="请求体类型")
    private String requestBodyType;
    
    @Schema(description="请求体")
    private String requestBody;

}

