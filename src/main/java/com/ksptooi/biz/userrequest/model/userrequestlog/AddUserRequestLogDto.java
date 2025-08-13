package com.ksptooi.biz.userrequest.model.userrequestlog;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class AddUserRequestLogDto {

    @Schema(description="用户请求记录ID")
    private Long id;

    @Schema(description="用户请求ID")
    private Long userRequestId;

    @Schema(description="请求ID")
    private String requestId;

    @Schema(description="请求方法")
    private String method;

    @Schema(description="请求URL")
    private String url;

    @Schema(description="来源")
    private String source;

    @Schema(description="请求头JSON")
    private String requestHeaders;

    @Schema(description="请求体长度")
    private Integer requestBodyLength;

    @Schema(description="请求体类型 JSON、表单数据、二进制")
    private String requestBodyType;

    @Schema(description="请求体JSON")
    private String requestBody;

    @Schema(description="响应头JSON")
    private String responseHeaders;

    @Schema(description="响应体长度")
    private Integer responseBodyLength;

    @Schema(description="响应体类型 JSON、表单数据、二进制")
    private String responseBodyType;

    @Schema(description="响应体JSON")
    private String responseBody;

    @Schema(description="HTTP响应状态码 -1为请求失败")
    private Integer statusCode;

    @Schema(description="重定向URL 301、302、303、307、308")
    private String redirectUrl;

    @Schema(description="状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时")
    private Integer status;

    @Schema(description="发起请求时间")
    private Date createTime;

    @Schema(description="响应时间")
    private Date responseTime;

}

