package com.ksptooi.biz.core.model.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter@Setter
public class GetRequestDetailsVo{

    //数据ID
    private Long id;

    //请求ID
    private String requestId;

    //请求方法
    private String method;

    //请求URL
    private String url;

    //来源
    private String source;

    //请求头
    private String requestHeaders;

    //请求体长度
    private Integer requestBodyLength;

    //请求体类型 JSON、表单数据、二进制
    private String requestBodyType;

    //请求体JSON
    private String requestBody;

    //响应头
    private String responseHeaders;

    //响应体长度
    private Integer responseBodyLength;

    //响应体类型 JSON、表单数据、二进制
    private String responseBodyType;

    //响应体JSON
    private String responseBody;

    //HTTP响应状态码
    private Integer statusCode;

    //重定向URL 301、302、303、307、308
    private String redirectUrl;

    //状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时
    private Integer status;

    //发起请求时间
    private LocalDateTime requestTime;

    //响应时间
    private LocalDateTime responseTime;

}
